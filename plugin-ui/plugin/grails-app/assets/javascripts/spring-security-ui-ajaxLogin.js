$(function() {
	var buttons = {};
	buttons[cancelButtonCaption] = function() {
		$(this).dialog('close');
	};
	buttons[loginButtonCaption] = function() {
		$('#loginForm').submit();
	};

	$("#loginFormContainer").dialog({
		autoOpen: false,
		height: 450,
		width: 450,
		modal: true,
		buttons: buttons
	});

	$('#loginForm').bind('submit', function() {
		$(this).ajaxSubmit({
			target: '#loginMessage',
			beforeSubmit: function() {
				$('#loginMessage').html(loggingYouIn);
				return true;
			},
			success: function(json) {
				if (json.success) {
					if (json.url) {
						document.location = json.url;
					}
					else {
						$('#loginFormContainer').dialog('close');
						$('#loginLinkContainer').html(
							loggedInAsWithPlaceholder.replace(/\{0\}/, json.username) +
							' (<a href="' + $("#_logout").attr("href") + '" id="logout">Logout</a>)');
						$("#logout").click(logout);
					}
				}
				else if (json.error) {
					$('#loginMessage').html("<span class='errorMessage'>" + json.error + '</error>');
				}
				else {
					$('#loginMessage').html(responseText);
				}
			},
			dataType: 'json'
		});
		return false;
	});

	$('#loginLink').click(function() {
		$('#loginFormContainer').show().dialog('open');
		$('#ajaxUsername').focus();
	});

   $("#logout").click(logout);
});

function logout(event) {
   event.preventDefault();
   $.ajax({
      url: $("#_logout").attr("href"),
      method: "post",
      success: function(data, textStatus, jqXHR) {
         window.location = $("#_afterLogout").attr("href");
      },
      error: function(jqXHR, textStatus, errorThrown) {
         console.log("Logout error, textStatus: " + textStatus + ", errorThrown: " + errorThrown);
      }
   });
}
