package com.example.quizapp


data class RankingEntry(
    val score: Int,
    val timestamp: String
)

data class Question(
    val question: String,
    val options: List<String>,
    val answerIndex: Int
) {
    // 중복 확인을 위한 equals/hashCode는 question 텍스트 기반
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Question) return false
        return question == other.question
    }
    
    override fun hashCode(): Int {
        return question.hashCode()
    }
}


object QuestionData {
     val data = mapOf(
        "역사" to listOf(
            Question("한글을 창제한 왕은?", listOf("세종", "세조", "성종", "문종"), 0),
            Question("세계 2차 대전이 끝난 연도는?", listOf("1939년", "1941년", "1945년", "1949년"), 2),
            Question("3·1 운동이 일어난 해는?", listOf("1910", "1915", "1919", "1925"), 2),
            Question("나라 외교권 빼앗은 인물은?", listOf("이토 히로부미", "고토 쇼지로", "고바야카와", "스티븐스"), 0),
            Question("임시정부 초대 대통령은?", listOf("윤봉길", "이승만", "김구", "안창호"), 1),
        ),
        "상식" to listOf(
            Question("지구 대기 중 가장 많은 기체는?", listOf("산소", "질소", "아르곤", "이산화탄소"), 1),
            Question("포유류가 아닌 것은?", listOf("돌고래", "박쥐", "펭귄", "고래"), 2),
            Question("한국 국제전화번호는?", listOf("+81", "+86", "+82", "+80"), 2),
            Question("제주도 삼다에 해당 안되는 것은?", listOf("여자", "돌", "바람", "해산물"), 3),
            Question("캐나다 수도는?", listOf("토론토", "밴쿠버", "오타와", "몬트리올"), 2),
        ),
        "과학" to listOf(
            Question("물의 화학식은?", listOf("H₂", "H₂O", "O₂", "NaCl"), 1),
            Question("태양계에서 가장 큰 행성은?", listOf("지구", "목성", "토성", "화성"), 1),
            Question("원자 번호 1번은?", listOf("산소", "헬륨", "수소", "탄소"), 2),
            Question("빛의 속도는?", listOf("300km/s", "300,000km/s", "30,000km/s", "3,000km/s"), 1),
            Question("인간 DNA는 몇 쌍?", listOf("23쌍", "46쌍", "12쌍", "92쌍"), 0),
        ),
        "영화" to listOf(
            Question("'기생충' 감독은?", listOf("봉준호", "박찬욱", "김기덕", "최동훈"), 0),
            Question("'겨울왕국' 엘사 여동생은?", listOf("안나", "올라프", "크리스토프", "마시멜로우"), 0),
            Question("아이언맨 배우는?", listOf("로버트 다우니 주니어", "크리스 에반스", "헴스워스", "톰 홀랜드"), 0),
            Question("한국 최고 관객 영화는?", listOf("명량", "신과함께", "극한직업", "기생충"), 0),
            Question("애니메이션 아닌 것은?", listOf("겨울왕국", "인사이드 아웃", "명량", "라이온킹"), 2),
        )
    )
    fun getQuestions(category: String): List<Question> = data[category] ?: emptyList()
}