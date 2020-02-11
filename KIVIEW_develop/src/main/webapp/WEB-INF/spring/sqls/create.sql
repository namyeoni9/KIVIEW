----------------------------------유치원 통합------------------------------
CREATE TABLE KINDER(
    KINDER_NO NUMBER PRIMARY KEY,
    NAME VARCHAR2(1000),
    TYPE VARCHAR2(1000),
    ADDR1 VARCHAR2(1000),
    ADDR2 VARCHAR2(1000),
    LATITUDE NUMBER,
    LONGITUDE NUMBER,
    PHONE VARCHAR2(1000),
    DIRECTOR VARCHAR2(1000),
    OPENDATE DATE,
    HOMEPAGE VARCHAR2(1000),
    CLASS_NUM NUMBER,
    CHILDREN_NUM NUMBER,
    STAFF_NUM NUMBER,
    DRIVE_YN VARCHAR2(1000),
    MEAL_YN VARCHAR2(1000),
    CCTV_YN VARCHAR2(1000)
    
);


CREATE TABLE PROVINCE(
    PROVINCE VARCHAR2(50),
    CITY VARCHAR2(50),
    TOWN VARCHAR2(50)
);

SELECT * FROM KINDER;
SELECT * FROM PROVINCE;


-----------------------------------공지 관련-----------------------------------

CREATE SEQUENCE NOTICE_SEQ;
--공지번호 제목 내용 작성자 등록일 조회수
CREATE TABLE NOTICE(
	NOTICE_NO NUMBER PRIMARY KEY,
	CAT_DETAIL VARCHAR2(10) NOT NULL,
	NOTICE_TITLE VARCHAR2(200) NOT NULL,
	NOTICE_CONTENT VARCHAR2(4000) NOT NULL,
	NOTICE_WRITER VARCHAR2(30) NOT NULL,
	NOTICE_DATE DATE,
	NOTICE_HIT NUMBER
);
CREATE SEQUENCE INTRO_SEQ;
--키뷰 소개 제목/내용/작성자/등록일/조회수
CREATE TABLE INTRO_TB(
	INTRO_NO NUMBER PRIMARY KEY,
	CAT_DETAIL VARCHAR2(10) NOT NULL,
	INTRO_TITLE VARCHAR2(200) NOT NULL,
	INTRO_CONTENT VARCHAR2(4000) NOT NULL,
	INTRO_WRITER VARCHAR2(30) NOT NULL,
	INTRO_DATE DATE,
	INTRO_HIT NUMBER
);
CREATE SEQUENCE FAQ_SEQ;
--키뷰 FAQ
CREATE TABLE FAQ(
	FAQ_NO NUMBER PRIMARY KEY,
	FAQ_CATD VARCHAR2(10) NOT NULL,
	FAQ_TITLE VARCHAR2(200) NOT NULL,
	FAQ_CONTENT VARCHAR2(4000) NOT NULL
);


------------------------------------회원 관련-----------------------------------

CREATE SEQUENCE MEMBER_SEQ;

--회원번호 아디 비번 이름 주소 연락처 이메일
CREATE TABLE MEMBER(
	MEMBER_NO NUMBER PRIMARY KEY,
	MEMBER_ID VARCHAR2(50) UNIQUE,
	MEMBER_PWD VARCHAR2(50) NOT NULL,
	MEMBER_NAME VARCHAR2(50) NOT NULL,
	MEMBER_ADDR VARCHAR2(200) NOT NULL,
	MEMBER_PHONE VARCHAR2(50) NOT NULL,
	MEMBER_EMAIL VARCHAR2(50) NOT NULL
);



-------------------------------------리뷰 관련----------------------------------------

CREATE SEQUENCE REVIEW_SEQ;

--리뷰번호, 유치원번호, 평점1,평점2,평점3,리뷰제목, 리뷰내용, 작성자, 등록일
CREATE TABLE REVIEW(
	REVIEW_NO NUMBER PRIMARY KEY,
	NAME VARCHAR2(200),
	AVG_SCORE1 NUMBER NOT NULL,
	AVG_SCORE2 NUMBER NOT NULL,
	AVG_SCORE3 NUMBER NOT NULL,
	REVIEW_TITLE VARCHAR2(200) NOT NULL,
	REVIEW_CONTENT VARCHAR2(4000) NOT NULL,
	REVIEW_WRITER VARCHAR2(50) NOT NULL,
	REVIEW_DATE DATE
);

-----------------------------------좋아요 관련----------------------------------

CREATE SEQUENCE LIKE_SEQ;

--좋아요번호 리뷰번호 회원번호
CREATE TABLE LIKETABLE(
	LIKE_NO NUMBER PRIMARY KEY,
	REVIEW_NO NUMBER REFERENCES REVIEW(REVIEW_NO),
	MEMBER_NO NUMBER REFERENCES MEMBER(MEMBER_NO),
	LIKEYN VARCHAR2(10)
);

----------------------------------즐겨찾기 관련--------------------------------------

CREATE SEQUENCE FAVORITE_SEQ;

--즐찾번호, 유치원번호, 회원번호
CREATE TABLE FAVORITE(
	FAVORITE_NO NUMBER PRIMARY KEY,
	KINDER_NO NUMBER REFERENCES KINDER(KINDER_NO),
	MEMBER_NO NUMBER REFERENCES MEMBER(MEMBER_NO),
	FAVORITEYN VARCHAR2(10)
);



---------------------------------------카페 관련-----------------------------------------

CREATE SEQUENCE CAFE_SEQ;

CREATE TABLE CAFE(
	CAFE_NO NUMBER PRIMARY KEY,
	TITLE VARCHAR2(200) UNIQUE,
	ADMIN VARCHAR2(50),
	INTRO VARCHAR2(1000),
	THUMB VARCHAR2(1000),
	BACKGROUND VARCHAR2(1000),
	RESTRICTION VARCHAR2(10)
);


CREATE SEQUENCE CAFE_MEMBER_SEQ;


CREATE TABLE CAFE_MEMBER(
	CAFE_MEMBER_NO NUMBER PRIMARY KEY,
	MEMBER_NO NUMBER REFERENCES MEMBER(MEMBER_NO),
	CAFE_NO NUMBER REFERENCES CAFE(CAFE_NO),
	ANSWER VARCHAR2(1000),
	SIGNYN VARCHAR2(10),
	BLOCKYN VARCHAR2(10),
	SIGNDATE DATE
	
);


CREATE SEQUENCE CAFE_MENU_SEQ;


CREATE TABLE CAFE_MENU(
	CAFE_MENU_NO NUMBER PRIMARY KEY,
	CAFE_NO REFERENCES CAFE(CAFE_NO),
	NAME VARCHAR2(50),
	AUTHORITY VARCHAR2(10),
	CONCEPT VARCHAR2(50),
	CONSTRAINT MENU_UK UNIQUE (CAFE_NO, NAME) 
);


CREATE SEQUENCE CAFE_CATEGORY_SEQ;


CREATE TABLE CAFE_CATEGORY(
	CAFE_CATEGORY_NO NUMBER PRIMARY KEY,
	CAFE_MENU_NO REFERENCES CAFE_MENU(CAFE_MENU_NO),
	CATEGORY VARCHAR2(50),
	CONSTRAINT CATEGORY_UK UNIQUE(CAFE_MENU_NO, CATEGORY)
);



CREATE SEQUENCE CAFE_BOARD_SEQ;


CREATE TABLE CAFE_BOARD(
	CAFE_BOARD_NO NUMBER PRIMARY KEY,
	CAFE_MENU_NO REFERENCES CAFE_MENU(CAFE_MENU_NO),
	CATERGORY VARCHAR2(50),
	TITLE VARCHAR2(200) NOT NULL,
	CONTENT VARCHAR2(4000) NOT NULL,
	WRITER VARCHAR2(50),
	REGDATE DATE,
	HIT NUMBER
);


CREATE SEQUENCE CAFE_REPLY_SEQ;


CREATE TABLE CAFE_REPLY(
	CAFE_REPLY NUMBER PRIMARY KEY,
	CAFE_BOARD_NO REFERENCES CAFE_BOARD(CAFE_BOARD_NO),
	WRITER VARCHAR2(50),
	CONTENT VARCHAR2(1000),
	REGDATE DATE
	

);




