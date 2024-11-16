package com.honsb.travel.domain.entity;

import com.honsb.travel.domain.enum_class.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
//해당 클래스가 JPA(Java Persistence API)에서 엔티티로 사용됨을 나타냄
//데이터베이스 테이블과 매핑되어, 이 클래스의 인스턴스가 테이블의 행(row)을 나타냄
@AllArgsConstructor
//모든 필드를 파라미터로 받는 생성자를 자동으로 생성
//이를 통해 객체 생성 시 모든 필드를 초기화할 수 있음
@NoArgsConstructor
//파라미터가 없는 기본 생성자를 자동으로 생성
//JPA에서 엔티티 객체를 생성할 때 기본 생성자가 필요하기 때문에 자주 사용
@Builder
//빌더 패턴을 적용할 수 있도록 해줌
//이를 통해 객체 생성 시 가독성을 높이고, 불변 객체를 쉽게 생성할 수 있음
//클래스.builder().필드명(값).build() 형식으로 객체를 생성할 수 있음
@Getter
//클래스의 모든 필드에 대해 Getter 메서드를 자동으로 생성
//이를 통해 getFieldName() 형태의 메서드를 일일이 작성하지 않아도 됨
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    //데이터베이스의 기본 키(primary key)를 설정하는 필드를 정의
    //이 어노테이션은 id 필드 값이 자동으로 생성되도록 설정
    //strategy는 기본 키 값이 생성되는 방식을 결정
    //GenerationType.IDENTITY는 데이터베이스의 "자동 증가(auto-increment)" 기능을 사용하여 고유한 ID를 생성
    //이 방식에서는 데이터베이스가 id 값을 자동으로 증가시키며, 직접 값을 설정하지 않아도 됨
    private Long id;

    private String loginId; // 로그인 아이디
    private String password; // 비밀번호
    private String nickname; // 닉네임
    private LocalDateTime createdAt; // 가입시간
    private Integer receivedLikeCnt; // 좋아요 개수 (본인꺼 제외)

    @Enumerated(EnumType.STRING)
    private UserRole userRole; // 권한

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Board> boards; // 작성글

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Like> likes; // 좋아요

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Comment> comments; //댓글



    public void rankUp(UserRole userRole, Authentication auth){
        this.userRole = userRole;

        List<GrantedAuthority> updatedAuthorities = new ArrayList<>();
        updatedAuthorities.add(new SimpleGrantedAuthority(userRole.name()));
        Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), updatedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    public void likeChange(Integer receivedLikeCnt){
        this.receivedLikeCnt = receivedLikeCnt;
        if(this.receivedLikeCnt >= 10 && this.userRole.equals(UserRole.SILVER))
            this.userRole = UserRole.GOLD;
    }

    public void edit(String newPassword, String newNickname){
        this.password = newPassword;
        this.nickname = newNickname;
    }

    public void changeRole(){
        if (userRole.equals(UserRole.BRONZE)) userRole = UserRole.SILVER;
        else if (userRole.equals(UserRole.SILVER)) userRole = UserRole.GOLD;
        else if (userRole.equals(UserRole.GOLD)) userRole = UserRole.BLACKLIST;
        else if (userRole.equals(UserRole.BLACKLIST)) userRole = UserRole.BRONZE;
    }

}
