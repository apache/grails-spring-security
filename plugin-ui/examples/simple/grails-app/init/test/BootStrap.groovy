package test

import groovy.transform.CompileStatic

@CompileStatic
class BootStrap {
    TestDataService testDataService

    def init = { servletContext ->
        testDataService.createData()
    }
    def destroy = {
    }
}
