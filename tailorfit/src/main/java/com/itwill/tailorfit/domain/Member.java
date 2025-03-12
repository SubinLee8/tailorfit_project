package com.itwill.tailorfit.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.NaturalId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@Getter
@ToString(callSuper = true) //super class인 createdtime, modifiedTime도 toString으로
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper=false) //@EqualsAndHashCode.Include로 지정된 필드만 가지고서 equalsAndHashCode를 만들겠다는 뜻
		
@Entity
@Table(name="members")
public class Member extends BaseTimeEntity implements UserDetails {
    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
	@EqualsAndHashCode.Include
	@Basic(optional=false)
	@NaturalId
    private String username;

    @Basic(optional=false)
    private String nickname;
    
    @Basic(optional=false)
    private String password;
    
    @Basic(optional=false)
    private String email;
    
    @Basic(optional=false)
    private String gender;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "plan")
    private WorkoutRoutine workoutRoutine; 
    
    @Builder.Default
    @ElementCollection(fetch=FetchType.LAZY)
    @Enumerated(EnumType.STRING) //테이블에 저장할 때 상수의 이름으로 저장
	private Set<MemberRole> roles=new HashSet<>();  
    
    @Basic(optional=false)
    @Column(name="birth_date")
    private LocalDate birthDate;
    
    @Column(name="has_body_metrics")
    private String hasBodyMetrics;
    
    @Column(name="is_connected_strava")
    private String isConnectedStrava;
    
    public Member addRole(MemberRole role) {
		roles.add(role); // HashSet<MemberRole> roles에 원소를 추가.
		return this;
	}
	
	public Member removeRole(MemberRole role) {
		roles.remove(role); // HashSet<MemberRole> roles에서 원소를 삭제.
		return this;
	}
	
	public Member clearRoles() {
		roles.clear(); // HashSet<MemberRole> roles의 모든 원소를 삭제.
		return this;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<SimpleGrantedAuthority> authorities=roles.stream().map(x->
				new SimpleGrantedAuthority(x.getAuthority())).toList();
		return authorities;
	}
	
	public Member updateStravaStatus(String isConnectedStrava) {
		this.isConnectedStrava=isConnectedStrava;
		return this;
	}
	
	public Member updateBodyStatus(String hasBodyMetrics) {
		this.hasBodyMetrics=hasBodyMetrics;
		return this;
	}
    
}





