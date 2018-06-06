package cn.nuaa.ai.word2vec;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        Word2VEC w1 = new Word2VEC() ;
        w1.loadGoogleModel("library/corpus.bin") ;
        
        System.out.println(w1.distance("�����"));
        
        System.out.println(w1.distance("ë��"));
        
        System.out.println(w1.distance("��Сƽ"));
        
        
        System.out.println(w1.distance("ħ����"));
        
        System.out.println(w1.distance("ħ��"));
        
    }
}
