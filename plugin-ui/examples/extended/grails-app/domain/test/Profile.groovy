package  test

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString


@EqualsAndHashCode(includes='user')
@ToString(includes='user', includeNames=true, includePackage=false)
class Profile implements Serializable {

    private static final long serialVersionUID = 1

     
    String myQuestion1
    String myAnswer1
     
    String myQuestion2
    String myAnswer2
     
    User user

     static constraints = {
         
             myQuestion1 nullable: true, blank: false
             myAnswer1 nullable: false, blank: false
         
             myQuestion2 nullable: true, blank: false
             myAnswer2 nullable: false, blank: false
         
            user nullable: false, blank: false, unique: true
        }

    static belongsTo = [ user: User]


}