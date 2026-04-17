package pages

import geb.Page

class AccessDeniedPage extends Page {

    static at = { $('h1').text() == 'Access Denied' }
}
