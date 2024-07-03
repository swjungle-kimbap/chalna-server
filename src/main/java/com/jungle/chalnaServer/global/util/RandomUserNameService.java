package com.jungle.chalnaServer.global.util;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RandomUserNameService {
    Random random = new Random();
    public static String[] PREFIXES = {
            "", "똑똑한 ", "훈훈한 ", "뜨거운 ", "차가운 ", "부지런한 ", "게으른 ", "답답한 ", "행복한 ", "화난 ", "안경쓴 ",
            "배고픈 ", "졸린 ", "수줍은 ", "귀찮은 ", "슬픈 ", "섹시한 ", "밝은 ", "용감한 ", "기쁜 ", "활기찬 ",
            "친절한 ", "관대한 ", "부드러운 ", "행복한 ", "정직한 ", "즐거운 ", "생기있는 ", "고귀한 ", "평화로운 ", "긍정적인 ",
            "빛나는 ", "믿음직한 ", "반짝이는 ", "강한 ", "활발한 ", "따뜻한 ", "현명한 ", "열정적인 ", "명랑한 ", "매력적인 ",
            "단정한 ", "온화한 ", "자상한 ", "희망찬 ", "청순한 ", "유쾌한 ", "적극적인 ", "자비로운 ", "긍적적인 ", "게으른 ",
    };

    public static String[] SUBFIXES = {
            "찰진밥","식은김","사과","배","바나나","토마토","당근","양파","감자","고구마",
            "딸기","포도","오렌지","수박","멜론","레몬","블루베리","브로콜리","시금치","호박",
            "콩","참깨","두부","미역","된장","고추장","김치","깍두기","나물","라면","떡"
    };

    public String getRandomUserName() {
        return PREFIXES[random.nextInt(PREFIXES.length)] + SUBFIXES[random.nextInt(SUBFIXES.length)];
    }
}
