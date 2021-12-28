# SimpleToDoList
스프링 프레임워크를 좀 더 공부하면서 React, Vue 등 클라이언트 라이브러리와 API를 기반으로 통신하며 화면을 그려내는 클라이언트 측 렌더링(CSR)에 대해 알게 되었습니다.
그리고 Roy Fielding이 제안한 REST 아키텍처를 준수하는 API, 즉 RESTful API에 대해서 알게 되어 이를 활용하는 API 서버 애플리케이션을 만들고자 제작한 애플리케이션입니다.

이번에도 동일하게 [Heroku에 배포](https://simple-todolist-springboot.herokuapp.com/docs/index.html)하였는데 SimpleBBS와는 달리 별도의 화면을 그려내지 않았기 때문에
링크에서는 아래처럼 API 문서를 대신 제공하고 있습니다.
<!-- ![image](https://user-images.githubusercontent.com/12295334/140961052-ead40ae9-e368-43fb-9f96-260cf6d81df1.png) -->
<img src="https://user-images.githubusercontent.com/12295334/140961052-ead40ae9-e368-43fb-9f96-260cf6d81df1.png" width="70%">

이 프로젝트는 [블랙커피 스터디](https://edu.nextstep.camp/s/YDLr4omo/)에서 진행했던 간단한 할 일 관리 애플리케이션과 상용 서비스 [Trello](https://trello.com/)에서 영감을 받아 진행하였습니다. 상용 서비스와 비교하면 상대적으로 기능이 부족하지만 그에 대등하고자 하는 것을 최종 목표로 삼고 그러면서도 완전히 기능을 베끼지 않고 SimpleTodoList 만의 특징을 갖고자 합니다.

## 구현 기능
- Spring MVC와 RESTful API를 통한 HTTP 요청, 응답 처리.
- JWT와 Spring Security 기반 인증, 인가 적용.
- 회원, 비회원 별 서비스 제공.
- 팀 > 할 일 리스트 > 할 일 관계로 기능 계층 구현.
  - 할 일 리스트에 할 일 CRUD 기능 구현.
  - 팀에 할 일 리스트 CRUD 기능 구현.
  - 각 구성 요소에 대한 인가 제어 구현.

## 후기 및 개선 사항
SimpleBBS가 일체 API를 구성하지 않았던 SSR 애플리케이션이었다면 SimpleTodoList는 일체 화면을 구성하지 않은 CSR 전용 애플리케이션입니다.
단순히 화면만 제공할 때는 HTTP 상태 코드나 헤더를 활용할 일이 거의 없었기 때문에 이에 대한 이해가 부족했지만 API를 기반으로 제공하면서 어떤 포맷(JSON, XML)과 내용으로
응답을 전달해야 클라이언트에서 활용할 수 있을지 고민해보고 HTTP 헤더의 활용(i.e. 201 Created의 Location 헤더), HTTP 상태 코드의 의미(i.e. 403 Forbidden)에 대해서
이해할 수 있는 계기가 되었습니다.

SimpleBBS로 스프링에 입문했다면 SimpleTodoList로 웹 개발에 입문했다고 봐도 좋은 프로젝트였다고 생각합습니다.

아쉬운 점은 현재 별도의 파일 업로드 기능이 구현되어 있지 않아 이미지, 동영상 등 할 일 생성 시 파일을 첨부할 수 없습니다. 그래서 추후 파일 업로드 기능 뿐 아니라 할 일의 만료 기한이나
사용자 할당 등 기능적인 측면에서도 계속 유지보수하며 덧붙여나가고자 합니다.

추가적으로 프로젝트를 보일 때 화면이 없어 곤란한 적이 종종 있었기 때문에 간단한 프론트엔드 라이브러리를 배워서 SimpleTodoList와 상호작용할 수 있는 프론트엔드 앱을 만들어보고자 합니다.

## 사용 기술 스택
- Spring Boot 2.x
  - Spring Framework에서 클래스패스의 라이브러리를 자동으로 인식하여 설정해주고 내장 서버를 제공하는 등 많은 편의성을 제공하기 때문에 빠른 개발이 가능하다고 생각하여 Spring Boot를 사용하였습니다.
- Spring Data JPA
  - Spring 애플리케이션에서 JPA의 구현체 중 하나인 Hibernate를 Spring Data Repository를 활용하여 간편하게 사용할 수 있는 라이브러리기 때문에 사용하였습니다.
- Spring Security
  - 애플리케이션이 회원 기능을 지원하기 때문에 이에 필수적인 인증, 인가 기능을 적용하기 위해 사용하였습니다.
  - JWT를 이용하여 토큰 기반 인증을 구현하기 위해 사용하였습니다.
- Spring Rest Docs
  - 기능 변경이 잦아지면서 테스트 코드의 중요성과 코드가 주는 변경에 대한 안전성을 실감할 수 있었습니다.
  - 스프링 프로젝트에서 공식적으로 제공하는 Spring Rest Docs 프로젝트는 테스트 코드를 기반으로 자동으로 API 문서를 생성할 수 있기 때문에 테스트 코드와 API 문서화 두 마리 토끼를 모두 잡고자 사용했습니다.
  - 그러나 Swagger에 비하여 디자인적인 측면에서 아쉬운 부분과 테스트 코드량이 증가하고 빌드 시간이 증가하는 등 단점 역시 존재하기 때문에 차후 Swagger도 사용해보고자 합니다.
- MariaDB 10.x
  - MySQL의 소유권이 Oracle이라는 기업에 넘어갔기 때문에 오픈 소스인 MariaDB를 사용하였습니다.
  - 역량이 부족하여 데이터베이스 튜닝 경험은 없습니다.
- JWT
  - 서버측에서 별도의 추가 인증 과정(데이터베이스 접근 등) 없이 인증을 할 수 있다는 장점과 Stateless한 환경에서 동작하는 API 서버 애플리케이션의 인증 수단으로 적합하다고 판단하여 사용했습니다.

## 개발 환경
- Java 11
- IntelliJ Ultimate(Educational License)
- Windows 10
- Lombok
- Junit 5
- Gradle 7.x
