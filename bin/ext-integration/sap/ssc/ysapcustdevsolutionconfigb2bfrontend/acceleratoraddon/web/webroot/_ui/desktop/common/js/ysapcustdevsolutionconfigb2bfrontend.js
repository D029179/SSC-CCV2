$(document).ready(function ()
{
	$('.submitRemoveSolutionProduct').on("click", function ()
			{
				var prodid = $(this).attr('id').split("_");
				var formid = '#updateSolutionForm' + prodid[1];
				var form = $(formid);
				form.find('input[name=productCode]').val(); 
				form.submit();
			});
	
});
