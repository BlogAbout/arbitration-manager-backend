package entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import dto.ERole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username")
})
public class User implements UserDetails {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String middleName;
    private String companyName;
    private String phone;
    private boolean noMiddleName;
    private boolean entity;
    private boolean enabled;
    private boolean locked;
    private int validationCode;
    private String inn;
    private String ogrn;
    private String kpp;

    @JsonIgnore
    private String password;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registration;

    @ElementCollection(targetClass = ERole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<ERole> role;

    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isNoMiddleName() {
        return noMiddleName;
    }

    public void setNoMiddleName(boolean noMiddleName) {
        this.noMiddleName = noMiddleName;
    }

    public boolean isEntity() {
        return entity;
    }

    public void setEntity(boolean entity) {
        this.entity = entity;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * <p>Проверяет, активная ли учетная запись пользователя.</p>
     * <p>Не активная учетная запись не может быть аутентифицирована.</p>
     * @return true, если учетная запись активна, false - если не активна.
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public LocalDateTime getRegistration() {
        return registration;
    }

    /**
     * <p>Устанавливает дату регистрации пользователя.</p>
     * <p>Дата регистрации не изменяется на протяжении всего существования пользователя.</p>
     * @param registration дата регистрации
     */
    public void setRegistration(LocalDateTime registration) {
        this.registration = registration;
    }

    public Set<ERole> getRole() {
        return role;
    }

    public void setRole(Set<ERole> roles) {
        this.role = roles;
    }

    public int getValidationCode() {
        return validationCode;
    }

    public void setValidationCode(int validationCode) {
        this.validationCode = validationCode;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getOgrn() {
        return ogrn;
    }

    public void setOgrn(String ogrn) {
        this.ogrn = ogrn;
    }

    public String getKpp() {
        return kpp;
    }

    public void setKpp(String kpp) {
        this.kpp = kpp;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRole();
    }

    /**
     * <p>Проверяет, истек ли срок действия учетной записи пользователя.</p>
     * <p>Учетная запись с истекшим сроком действия не может быть аутентифицирована.</p>
     * @return true, если учетная запись действительна, false - если срок действия истёк.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * <p>Проверяет, заблокирована ли учетная запись пользователя.</p>
     * <p>Заблокированная учетная запись не может быть аутентифицирована.</p>
     * @return true, если учетная запись не заблокирована, false - если заблокирована.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * <p>Проверяет, истек ли срок действия учетных данных пользователя.</p>
     * <p>Учетная запись с истекшим сроком действия учетных данных не может быть аутентифицирована.</p>
     * @return true, если учетные данные действительны, false - если срок действия истёк.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        User other = (User) obj;

        if (id == null)
            return other.id == null;
        else
            return id.equals(other.id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", companyName='" + companyName + '\'' +
                ", phone='" + phone + '\'' +
                ", noMiddleName=" + noMiddleName +
                ", entity=" + entity +
                ", enabled=" + enabled +
                ", locked=" + locked +
                ", validationCode=" + validationCode +
                ", inn='" + inn + '\'' +
                ", ogrn='" + ogrn + '\'' +
                ", kpp='" + kpp + '\'' +
                ", password='" + password + '\'' +
                ", registration=" + registration +
                ", role=" + role +
                '}';
    }
}