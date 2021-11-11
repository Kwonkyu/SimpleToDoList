# SimpleToDoList
단순함(Simple)이라는 가치로 모두가 쉽게 사용할 수 있는 할 일 관리 애플리케이션 SimpleTodoList 입니다.
[이 곳](https://simple-todolist-springboot.herokuapp.com/docs/index.html)에서 API 문서를 제공하고 있습니다.
![image](https://user-images.githubusercontent.com/12295334/140961052-ead40ae9-e368-43fb-9f96-260cf6d81df1.png)

스프링 프레임워크를 활용한 두 번째 개인 프로젝트로 이번에는 SimpleBBS 같은 SSR이 아닌 API 서버를 개발했습니다. 특정 프론트엔드 구현 방식을 염두에 두고 진행하지 않았기 때문에
추후 API 스펙이 변경될 수 있습니다.

애플리케이션 자체는 [블랙커피 스터디](https://edu.nextstep.camp/s/YDLr4omo/)에서 사용했던 API 서버와 상용 서비스 [Trello](https://trello.com/)에서 영감을 받아 진행하고 있습니다.

## 기능
### 회원
사용자는 회원가입한 후 로그인으로 JWT를 받아 본인을 인증할 수 있습니다. 이후 팀이나 할 일 리스트, 할 일 항목을 생성하거나 수정할 수 있습니다.

### 팀
사용자는 자신만의 팀을 꾸려 보고싶은 할 일 리스트만 관리할 수 있습니다. 그리고 다른 사용자를 팀에 초대하거나 팀장 자리를 위임할 수 있으며 잠금 상태로 설정하여 추가적인 가입을 제한할 수 있습니다.

### 할 일 리스트
사용자는 팀에 속해있다면 할 일 리스트를 만들어서 공유할 수 있습니다. 할 일 리스트는 다른 사람들이 얼마든지 조회하고 변경하거나 삭제할 수 있습니다.

이를 원치 않는다면 할 일 리스트를 잠금 상태로 변경할 수 있으나 팀장은 여전히 할 일 리스트를 조작할 수 있습니다.

### 할 일
할 일 리스트를 만들었다면 사용자는 자신이 할 일을 추가할 수 있습니다. 지금은 '단순함'이라는 가치에 맞게 제목과 내용만 등록할 수 있지만 점차 기능을 추가하면서 만료 기한이나 파일 업로드 등 다양한 기능을 지원할 예정입니다.

할 일 항목 역시 기본적으로는 다른 사람들이 수정하고 삭제할 수 있습니다. 이를 원치 않는다면 할 일 항목을 잠금 상태로 변경할 수 있으나 팀장은 여전히 할 일 항목을 조작할 수 있습니다.

## 기술적 목표
- 스터디에서 진행했던 서버보다 심화된 기능을 가진 API 서버 구현.
  - [X] 팀, 할 일 리스트, 할 일 관계 정립 및 인가 구현.
- RESTFul API 반영
  - [X] Level 3 RESTful 적용.
  - [ ] Spring HATEOAS 를 이용한 Level 4 RESTful 적용.
- Spring Security를 활용한 보안 정책 반영.
  - [X] 로그인, 회원가입 등 회원제 적용.
  - [X] 허가되지 않은 리소스 접근, 수정 제한(가입하지 않은 팀, 삭제할 수 없는 할 일 등)
- JWT 토큰 활용
  - [X] JWT를 활용한 사용자 인증 구현.
  - [ ] Refresh Token 기능 적용.
- OAuth 적용
  - [ ] OAuth를 이용한 사용자 인증.
- 프론트엔드 페이지 구현
  - [ ] Vue.js를 이용한 프론트엔드 페이지 구현.

개발 진행 사항은 [JIRA](https://simpletodolist.atlassian.net/jira/software/projects/STDL/boards/1)에서 관리하고 있습니다.

## Tech Stacks
- [X] Spring Framework 5.x
- [X] Spring Boot 2.x
- [X] Spring Data JPA
- [X] Spring Security
- [X] Spring Rest Docs
- [ ] 
- [ ] Spring HATEOAS
