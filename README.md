# Compose Basic Camera Sample Code

## [기능 설명 및 앱 동작 Preview]

### ‣ 기능 설명
제가 필요한 카메라의 기본 기능(촬영, 프리뷰 표시)을 Jetpack Compose로 구현한 샘플 코드입니다.
실시간 카메라 프리뷰, 사진 촬영 기능, 전면/후면 카메라 전환 기능을 포함합니다.
현재 사진을 파일로 저장하는 기능을 넣을지말지 고민 중입니다. 기능 추가 자체는 어렵지 않은데, 괜히 샘플용 앱에서 용량 잡아먹는 기능을 추가하는게 맞나 싶어서 말이죠.


[사용법]
프리뷰 화면이나 중앙 하단의 촬영 버튼을 눌러 사진을 촬영합니다.
하단 우측의 전환 버튼을 눌러 전/후방 카메라를 전환합니다.

### ‣ Preview

1. 후방 카메라
  <img src="https://github.com/parade621/Compose_Basic_Camera/assets/36446270/3467d292-fe8b-4f63-8626-02186ea14204" width="50%" height="50%">

2. 전방 카메라
  <img src="https://github.com/parade621/Compose_Basic_Camera/assets/36446270/fdbcae99-5ae9-4bb3-9ba6-ba779ba2858c" width="50%" height="50%">

<br>

### 구현 방법 및 기술 스택
- Jetpack Compose
- MVI pattern
- Hilt
- Android CameraX
- Coil (추가 예정)
