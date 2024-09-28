package com.grad.akemha.entity;

import com.grad.akemha.entity.enums.Gender;
import com.grad.akemha.entity.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@NotNull(message = "Name cannot be null")
    @NotBlank(message = "Please add a name")
    @Column(nullable = false)
    private String name; // for beneficiary and doctor

    @Column(nullable = false, unique = true)
    private String email;

    @Column(unique = true, name = "phone_number")
    private String phoneNumber; // for beneficiary and doctor

    @Column(nullable = false)
    private String password; // for beneficiary and doctor

    //Creation Date
    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "dob", nullable = true)
    private LocalDate dob; // for beneficiary and doctor

    @Column(name = "profile_image", nullable = true)
    private String profileImage; // for beneficiary and doctor

    @Column(name = "is_active", nullable = true)
    private Boolean isActive = true;

    @Column(name = "description", nullable = true)
    private String description; // for doctor

    @Enumerated(EnumType.STRING)
    private Gender gender; // for beneficiary and doctor

    @Column(name = "location", nullable = true)
    private String location; // for doctor

    @Column(name = "opening_times", nullable = true)
    private String openingTimes; // for doctor

    // to make sure this account is verified
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified;

    @Column(name = "device_token")
    private String deviceToken;

    //    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "specialization_id")
    private Specialization specialization;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

//    @OneToMany(cascade = CascadeType.ALL) //fetch = FetchType.LAZY, cascade = CascadeType.ALL
////    @JoinColumn(name = "user_id")
////    private List<Like> likes;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return null;
        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
        String role = String.valueOf(getRole());
        list.add(new SimpleGrantedAuthority("ROLE_" + role));
        System.out.println("list is" + list);
        return list;
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return getIsActive();
    }

}
