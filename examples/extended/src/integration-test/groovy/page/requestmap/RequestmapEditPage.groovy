package page.requestmap

import geb.module.TextInput
import page.EditPage

class RequestmapEditPage extends EditPage {

	static url = 'requestmap/edit'
	static typeName = { 'Requestmap' }
	static content = {
		configAttribute { $(name: 'configAttribute').module(TextInput) }
		urlPattern { $(name: 'url').module(TextInput) }
	}
}
