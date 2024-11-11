# ⚡ Backend Onboarding Assignment(Java)

## API 명세

- 회원가입 : http://52.78.151.97/signup  /
  {
  "username": "JIN HO",
  "password": "12341234",
  "nickname": "Mentos"
  }


- 로그인 : http://52.78.151.97/sign / {
  "username": "JIN HO",
  "password": "12341234"
  }

- 로그아웃 : http://52.78.151.97/logout

- 본인 정보 조회 : http://52.78.151.97/users

---

## 설명

- Security와 JWT에 대한 이해를 바탕으로 인증, 인가를 구현하였습니다.
- 인증, 인가에 관한 DB 조회는 로그인 시 한 번만 이뤄지며, 이후는 JWT로 인증, 인가를 하였습니다.
- Access Token은 블랙리스트, Refresh Token은 화이트리스트로 관리하였습니다.
  <br><br>
- 토큰 발급 및 검증에 대한 테스트 코드 통과
- Swagger 설정

---

### 로그인 요청

- DB에서 유효한 사용자임이 확인되면, Access와 Refresh토큰을 발급
- Refresh 토큰은 **Redis에 화이트리스트 등록**

---

### 로그인 이후 API 요청

- Access토큰 블랙리스트 확인.
- 블랙리스트에 없다면 유효성 검사 후 인증 처리

#### Access토큰 유효성 검사 시 만료된 토큰일 때, 자동으로 토큰 재발급

- Refresh토큰 화이트리스트 조회
- Cache Hit 시 유효성 검사 진행
- Access, Refresh 토큰 모두 재발급
- 이 때, Refresh토큰 화이트리스트 갱신

---

### 로그아웃 요청

- Access토큰 블랙리스트 처리
- Refresh토큰 화이트리스트에서 삭제
