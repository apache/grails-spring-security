package test

class TestDataController {

	def mailSender
	TestDataService testDataService

	def reset() {
		testDataService.reset()
		render 'OK'
	}

	def updateMailSenderPort(int port) {
		mailSender.port = port
		render 'OK: ' + port
	}
}
