# 사용 기술 스택
### Frontend
- Javascript
- React (VITE)
- TailwindCSS
- Axios
### Backend
- Java
- Spring Boot
- Spring Data JPA
- ODAFileConverter : DWG to DXF 변환
- Kabeja : DXF 파일 파싱 및 객체 데이터 추출
### Database
- MySQL
<br></br>

# 개발 진행 상황
### Frontend
1. File Upload 기능 : axios 활용, DWG 파일 백엔드로 전송 기능 구현 완료
2. Backend Processing 기능 : 구현 완료
3. Visualization : 구현 실패

### Backend
1. DWG -> DXF 변환 : ODAFileConverter 이용 변환 및 저장 구현 완료
   - DWG : uploads 폴더에 저장
   - DXF : dxfFiles 폴더에 저장
2. DXF 파일로 부터 객체 데이터 추출
   - Kabeja 라이브리러 활용해 객체 데이터 추출 성공, 그러나 정제되지 않음.
3. 추출 데이터에 따라서 Entity와 DTO를 구성하여 Spring Data JPA로 MySQL에 저장하려 했으나 하지 못 함.
4. DXF -> SVG 변환 : Kabeja 라이브러리로 구현하려 했으나 하지 못 함.

<br></br>
- 프론트엔드 구동 영상

https://github.com/user-attachments/assets/1b712b67-d87a-4339-8cd7-46c5d82de0dd

<br></br>
- 백엔드 DXF파일로 부터 데이터 추출 캡쳐 이미지
![Screenshot 2025-05-30 at 09 35 53](https://github.com/user-attachments/assets/74f2ceaa-c127-482e-84e5-485c68125131)

