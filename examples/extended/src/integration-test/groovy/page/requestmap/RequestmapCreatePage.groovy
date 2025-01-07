package page.requestmap

import geb.module.TextInput
import page.CreatePage

class RequestmapCreatePage extends CreatePage {

	static url = 'requestmap/create'
	static typeName = { 'Requestmap' }
	static content = {
		configAttribute { $(name: 'configAttribute').module(TextInput) }
		urlPattern { $(name: 'url').module(TextInput) }
	}
}
