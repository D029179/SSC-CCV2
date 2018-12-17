"use strict";

var ACC = ACC || {};
CPQ.config.updateContent = function (response) {
    var $deferred = new $.Deferred(),
        counter = 2,
        checkIfDone = function () {
            counter--;
            !counter && $deferred.resolve();

            CPQ.devMode && ACC.sapcustomized.handleConfigurationDevMode();
        };

    var analyticState;
    if ($("#analyticsEnabled").text() === "true") {
        analyticState = CPQ.uihandler.storeState('.cpq-csticValueAnalytics');
    }
    var valuePriceState;
    var valuePriceDDLB;
    if ($("#asyncPricingMode").text() === "true") {
        valuePriceState = CPQ.uihandler.storeState('.cpq-csticValueDeltaPrice', function (obj, item) {
            item.labelId = obj.prev().attr('id');
            item.html = obj.wrap('<p/>').parent().html();
        });
        valuePriceDDLB = CPQ.uihandler.storeState('select[id*=ddlb]');
    }
    ACC.sapcustomized.updateSlotContent(response, 'configContentSlot', ["#configurationForm", ".cpq-button-bar"]).done(checkIfDone);
    if (analyticState) {
        CPQ.uihandler.restoreState(analyticState, function (item) {
            var div = $(CPQ.core.encodeId(item.id));
            if (div.length > 0) {
                div.html(item.html);
                div.removeClass('cpq-csticValueAnalyticsTemplate').addClass('cpq-csticValueAnalytics');
            }
        });
    }
    if (valuePriceState) {
        CPQ.uihandler.restoreState(valuePriceState, function (item) {
            var label = $(CPQ.core.encodeId(item.labelId));
            label.after(item.html);
        });
        CPQ.core.valuePriceDropHandle = setTimeout(function () {
            $('.cpq-csticValueDeltaPrice').remove();
        }, 250);
    }
    if (valuePriceDDLB) {
        CPQ.uihandler.restoreState(valuePriceDDLB, function (item) {
            var select = $(CPQ.core.encodeId(item.id));
            var values = select.children();
            var oldValues = $.parseHTML("<div>" + item.html + "</div>");
            $.each(values, function (id, value) {
                var oldValue = $(oldValues).find("option[id='" + value.id + "']");
                if (oldValue && oldValue.text().match(CPQ.config.ddlbValuePriceRegEx)) {
                    $(value).text(oldValue.text());
                }
            });
        });
        CPQ.core.valuePriceDropdownDropHandle = setTimeout(function () {
            var dropDowns = $('select[id*=ddlb]');
            $.each(dropDowns, function (id, ddlb) {
                $.each(dropDowns.children(), function (counter, optionValue) {
                    var textOfCsticValue = optionValue.text;
                    textOfCsticValue = textOfCsticValue.replace(CPQ.config.ddlbValuePriceRegEx, "");
                    optionValue.text = textOfCsticValue;
                });
            });
        }, 250);
    }

    ACC.sapcustomized.updateSlotContent(response, 'configSidebarSlot', ["#configMenu", "#priceSummary"]).done(checkIfDone);

    var varaiantSearchState = CPQ.uihandler.storeState('#configVariantSearchResults');
    var priceSummaryState;
    if ($("#asyncPricingMode").text() === "true") {
        priceSummaryState = CPQ.uihandler.storeState('#basePriceValue, #currentTotalValue, #selectedOptionsValue');
    }
    CPQ.uihandler.updateSlotContent(response, "configBottombarSlot");
    if (priceSummaryState) {
        CPQ.uihandler.restoreState(priceSummaryState);
        CPQ.core.priceSummaryDropHandle = setTimeout(function () {
            $('#basePriceValue, #currentTotalValue, #selectedOptionsValue').text('-');
            $('#basePriceValue, #currentTotalValue, #selectedOptionsValue').prop('title', '-');
        }, 250);
    }
    CPQ.uihandler.restoreState(varaiantSearchState);

    CPQ.uihandler.updateSlotContent(response, "cpq-message-area");
    CPQ.config.checkValueHasChanged();
    return $deferred.promise();
};

CPQ.core.encodeId = function (id) {

    function fcssescape(ch, asCodePoint) {
        if (asCodePoint) {
            if (ch === "\0") {
                return "\uFFFD";
            }
            return ch.slice(0, -1) + "\\" + ch.charCodeAt(ch.length - 1).toString(16) + " ";
        }

        return "\\" + ch;
    }

    // This method is taken from jQuery 3.xx - not present in jq 2.1.1 which is used on parts selector and config pages
    function escapeSelector(sel) {
        return (sel + "").replace(/([\0-\x1f\x7f]|^-?\d)|^-$|[^\0-\x1f\x7f-\uFFFF\w-]/g, fcssescape);
    }

    console.log("id=" + id);
    return "#" + escapeSelector(id);
};

ACC.sapcustomized = {
    updateSlotContent: function (response, slotName, itemsToUpdate) {
        var $deferred = new $.Deferred(),
            counter = itemsToUpdate.length,
            newSlotContent = CPQ.uihandler.getNewSlotContent(response, slotName);

        $.each(itemsToUpdate, function (index, item) {
            var contentToUpdate = $(newSlotContent).find(item);

            var updatePopularityValues = function (callback) {
                if (slotName == "configContentSlot" && typeof ACC.popularityoptions != "undefined") {
                    ACC.popularityoptions.update(contentToUpdate).done(function (content) {
                        contentToUpdate = content;
                        callback();
                    });
                } else {
                    callback();
                }
            };

            updatePopularityValues(function () {
                $('#' + slotName + " " + item).replaceWith(contentToUpdate);
                counter--;
                !counter && $deferred.resolve();
            });
        });

        return $deferred.promise();
    },

    handleConfigurationDevMode: function () {
        var cssClass = "devmode";
        $(".cpq-config-page").addClass(cssClass);

        var displayCsticAndValue = function (csticId, $item) {
            $item.addClass(cssClass);
            var valueArray = typeof $item.attr("id") != "undefined" ? $item.attr("id").split(".") : [];
            var value = $item.is("label, :checkbox") ? valueArray[valueArray.length - 2] : $item.val();
            var possibleValues = "";

            if ($item.is("select")) {
                possibleValues = " <span>POSSIBLE VALUES:</span> ";
                $("option", $item).each(function (index, option) {
                    possibleValues += (index > 0 ? ", " : "") + $(option).val();
                });
            }

            var valuesText = possibleValues + (typeof value != "undefined" && value.length ? " <span>VALUE:</span> " + value : "");
            var $text = $("<span>").addClass("devmode").html("<span>CSTIC:</span> " + csticId + valuesText);

            switch ($item.attr("type")) {
                case "radio":
                    $("+label", $item).append($text);
                    break;
                case "checkbox":
                    $("+input+label", $item).append($text);
                    break;
                case "text":
                case undefined:
                    $item.after($text);
            }
        };

        $(".cpq-cstic").each(function (i, cstic) {
            var idArray = $(".cpq-label-config-link-row label", cstic).attr("id").split(".");
            var csticId = idArray[idArray.length - 2];

            $("input:not(:hidden), select, .cpq-csticValueImageLabel, .cpq-csticValueLabelWithoutSelect", cstic).each(function (j, item) {
                var $item = $(item);
                $item.hasClass(cssClass) || displayCsticAndValue(csticId, $item);
            });
        });

        $(".cpq-config-page span.devmode").on("click", function (event) {
            event.preventDefault();
            event.stopImmediatePropagation();
        });
    }
};
$(document).ready(function ()
{
    $('.submitRemoveSolutionProduct').on("click", function () {
        var prodid = $(this).attr('id').split("_");
        var formid = '#updateSolutionForm' + prodid[1];
        var form = $(formid);
        form.find('input[name=productCode]').val();
        form.submit();
    });

    CPQ.devMode && ACC.sapcustomized.handleConfigurationDevMode();
});
