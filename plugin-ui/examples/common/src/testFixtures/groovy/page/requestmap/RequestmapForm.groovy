package page.requestmap

import groovy.transform.Immutable

import geb.Page
import geb.module.TextInput

@Immutable
class RequestmapForm {

    String configAttribute
    String urlPattern

    <P extends Page> void applyTo(P page) {
        if (configAttribute != null) page.$(name: 'configAttribute').module(TextInput).text = configAttribute
        if (urlPattern != null) page.$(name: 'url').module(TextInput).text = urlPattern
    }
}
