import org.springframework.security.web.access.AccessDeniedHandlerImpl
import com.testacl.UserPasswordEncoderListener

beans = {
	accessDeniedHandler(AccessDeniedHandlerImpl)
	userPasswordEncoderListener(UserPasswordEncoderListener, ref('hibernateDatastore'))
}