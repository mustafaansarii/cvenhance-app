import com.cvenhance.common.security.JwtAuthenticationFilter;
import com.cvenhance.common.security.AuthCookies;
import com.cvenhance.common.audit.AuditAction;
public class Test {
    public static void main(String[] args) {
        System.out.println(java.util.Arrays.toString(JwtAuthenticationFilter.class.getConstructors()));
        System.out.println(java.util.Arrays.toString(AuthCookies.class.getConstructors()));
        System.out.println(java.util.Arrays.toString(AuditAction.values()));
    }
}
