package grails.plugin.springsecurity.ui

class SecurityQuestionsCommand implements CommandObject, grails.validation.Validateable  {
    String username
    List<HashMap> validations
      static constraints = {
          validations nullable: true
    }
}