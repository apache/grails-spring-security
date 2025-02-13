package page

abstract class EditPage extends AbstractSecurityPage {

	static at = { title == "Edit ${typeName()}" }
	static content = {
		form { $('editForm') }
		submitBtn { $('a', id: 'update') }
	}

	void delete() {
		js.exec('document.forms.deleteForm.submit()')
	}
}
