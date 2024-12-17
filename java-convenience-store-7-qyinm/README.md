# java-convenience-store-precourse
## 프로그램 순서도
### SetUp
- md 파일 파싱 후 Repository 저장
### 유저 아이템 입력
- InputView: 유저 구매하려는 물품 입력
  - 유효 패턴(소괄호는 옵션): \[{문자열}-{숫자}](,[{문자열}-{숫자}])
- 입력값 파싱 후 Controller로 반환
### 주요 로직
- 유저가 구하려고 하는 물품 Cart(장바구니) 객체로 생성
  - 프로모션 적용 상품
    - 적용 여부 확인: 현재 날짜 확인
    - 재고 수량 >= 사려는 수량: 
      - 프로모션 적용이 가능한 상품에 대해 고객이 해당 수량만큼 가져오지 않았을 경우
        - 프로모션 혜택 출력, 추가 여부
        - 가져와야하는 수량: (사려는 수량 + 사려는 수량 / buy * get) % (buy+get)
    - 재고 수량 < 사려는 수량: 정가 구매의사 여부 확인(InputView)
      - 공통: 현재 최대 수량의 프로모션 상품 장바구니 추가
      - 구매의사 존재: 남은 개수만큼 일반 상품 장바구니 추가
        - 일반 상품 재고 < 구매 수량: 예외 발생
  - 일반 상품: 장바구니 수량만큼 추가
    - 일반 상품 재고 < 구매 수량: 예외 발생
- 구매
  - 실제 상품 Product 개수 차감
  - 멤버쉽 할인 적용 여부
    - 적용 시: 30% 할인
      - 적용 기준: 프로모션 할인 받지 않은 상품만 적용 가능
    - 미적용 시: 현재 장바구니 가격
  - 영수증 출력
  - 추가 구매 여부
    - 추가 구매 시: 유저 아이템 입력부터 다시 시작
    - 아닐 시: 프로그램 종료

## 기능 구현 TODO
### InputView
- [X] 유저 구매 할 아이템 입력
  - 유효 입력 패턴(소괄호는 옵션): \[{문자열}-{숫자}](,[{문자열}-{숫자}])
- 의사 여부 확인 입력
  - 공통 유효 입력 패턴: [Y|N]
  - [X] 정가 구매의사 여부 확인 입력
  - [X] 프로모션 적용 가능 상품 수량 만큼 가져오지 않을 때 추가 여부 확인 입력
  - [X] 멤버십 할인적용 여부 확인 입력
  - [X] 추가 구매의사 여부 확인 입력
### OutputView
- [X] 프로그램 실행 시 현재 상품 재고 출력문:
```
안녕하세요. W편의점입니다.
현재 보유하고 있는 상품입니다.

- {상품 명} {상품 가격}원 {상품 재고}개 {프로모션 명}
```
- [x] 유저 사려는 물품 입력 시 출력문: `구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])`
- [x] 정가 구매의사 여부 확인 입력 시 출력문: `현재 콜라 4개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)`
- [X] 프로모션 적용 가능 상품 수량 만큼 가져오지 않을 때 추가 여부 확인 입력시 출력문: `현재 {상품명}은(는) {수량}개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)`
- [x] 멤버십 할인적용 여부 확인 입력 시 출력문: `멤버십 할인을 받으시겠습니까? (Y/N)`
- [X] 영수증 출력:
```
예시:

==============W 편의점================
상품명		수량	금액
콜라		10 	10,000
=============증	정===============
콜라		2
====================================
총구매액		10	10,000
행사할인			-2,000
멤버십할인			-0
내실돈			 8,000
```
- [x] 추가 구매의사 여부 확인 입력 시 출력문: `감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)`
### Controller
- [X] 현재 재고 상태 출력
- [X] 장바구니 생성
- [X] 계산

### Service
- [X] 상품명 인자 전달 -> 프로모션 적용중인지 여부 확인
- [X] 상품명, 수량 인자 전달 -> 구매 가능 일반 상품 수량 여부 확인 반환
- [X] 상품명, 수량 인자 전달 -> 구매 가능 프로모션 상품 수량 여부 확인 반환
- [X] 상품명, 수량 인자 전달 -> 프로모션 적용 가능 상품 수량 만큼 더 가져 올 수 있는지 여부 확인 반환
- [X] 상품명, 수량 인자 전달 -> 프로모션, 일반 상품과 개수 함께 List 반환
- [X] 상품명, 수량 인자 전달 -> 수량 만큼 일반 상품 반환
- [X] 상품명, 수량 인자 전달 -> 수량 만큼 프로모션 상품 반환
- [X] 상품명 인자 전달 -> 상품명 존재 하지 않을 시 예외 발생
- [X] 상품명, 수량 인자 전달 -> 재고 수량이 구매 수량보다 적으면 예외 발생
- [X] 전체 상품 반환
- [X] 계산 기능
  - [X] 유저 구매할 상품들(List) 인자전달 -> 총 가격 계산
  - [X] 유저가 받을 수 있는 프로모션 추가 상품 계산
  - [X] 유저가 받을 수 있는 프로모션 추가 상품으로 인한 할인 계산
  - [X] 유저가 받을 수 있는 일반 제품에 대한 멤버십 할인 계산(30% 최대 8,000원까지)

### Repository
- [X] 상품명, 수량 인자 전달 -> 상품명에 해당하는 일반 상품 재고 수량만큼 차감후 상품 반환
- [X] 상품명, 수량 인자 전달 -> 상품명에 해당하는 프로모션 상품 재고 수량만큼 차감후 상품 반환
- [X] 상품명 인자 전달 -> 상품명에 해당하는 현재 일반 상품 재고상태 상품 반환
- [X] 상품명 인자 전달 -> 상품명에 해당하는 현재 프로모션 상품 재고상태 상품 반환
- [X] 전체 상품 반환

### Parser
- [X] promotions.md, products.md 파싱 후 repository 객체 저장

## SequenceDiagram
```mermaid
sequenceDiagram

    autonumber
    StoreRepository->>MarkdownParser: products.md 및 promotions.md 파싱 요청
    MarkdownParser->>StoreRepository: 상품 및 프로모션 데이터를 객체로 저장

    break 추가 구매 하지 않을 시 프로그램 종료
    InputView->> User: 유저 구매할 상품 요청 [상품명-수량]
    User->>InputView: 유저 구매할 상품 입력
    alt 유효하지 않은 입력
    InputView-->>InputView: 예외 발생, 재 입력
    end
    InputView->>StoreController: 유저 입력 데이터 인자 전달
    loop 유저 입력 데이터 Item으로 변경
    StoreController->>StoreService: 구매 상품명, 구매 수량 전달
    alt 일반, 프로모션 상품 재고 합 < 구매수량
    InputView->>StoreController: 재입력
    end
    StoreController->>StoreService: 상품명 전달
    StoreService->>StoreController: 해당 상품명 적용되는 프로모션 상품 있는 지 여부 반환
    alt 적용되는 프로모션 상품 없을 때
    StoreController ->> Item: 일반 상품 Item 생성
    end
    StoreController ->> StoreService: 상품명, 상품개수 전달
    StoreService ->> StoreController: 프로모션 상품 개수 구매 가능 여부
    alt 프로모션 상품으로만 구매 가능
    StoreController ->> Item: 프로모션 상품 Item 객체 생성
    else 일반 상품, 프로모션 상품 섞어서 Item 생성
    StoreController ->> InputView: 정가 가격 구매 여부 유저 입력 요청
    InputView ->> StoreController: 정가 가격 구매 여부 전달
    alt 정가 구매 의사 존재
    StoreController ->> StoreService: 상품명, 필요 개수, 프로모션 상품 전달 일반, 프로모션 상품 Item 요청
    StoreService ->> StoreController: 일반, 프로모션 상품으로 Item이 총 2개 있는 List 반환
    else 정가 구매 의사 없을 시
    StoreController ->> Item: 프로모션 상품 Item만 생성
    end
    end
    end
    StoreController ->> Cart: 위에서 생성한 Item으로 유저 장바구니 카트 생성
    Cart ->> StoreController: 유저 구매 예정 장바구니 카트 반환
    StoreController ->> ReceiptDto: 영수증 출력을 위한 객체 생성 요청
    ReceiptDto ->> StoreController: 영수증 출력용 Dto 반환
    StoreController ->> InputView: 멤버십 할인 여부 입력 요청
    InputView ->> StoreController: 멤버십 할인 여부 반환
    alt 멤버십 할인 여부 존재
    StoreController ->> StoreService: 멤버십 할인 요청
    StoreService ->> StoreController: 멤버십 할인 금액반환 최대 8000원
    end
    StoreController ->> OutputView: Cart와 ReceiptDto 전달
    OutputView ->> User: 영수증 출력
    StoreController ->> InputView: 추가 구매 여부 입력 요청
    InputView ->> StoreController: 추가 구매 여부 반환
    end
```