package java_bingo_game;

import java.util.Objects;

public class Word {
	private String eng;
	private String kor;
	
	public Word(String eng, String kor) {
		super();
		this.eng = eng;
		this.kor = kor;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Word)) return false;
		Word word = (Word) o;
		return Objects.equals(eng, word.eng) && Objects.equals(kor, word.kor);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub		
		return eng + "(" + kor + ")";
	}

	public String getEng() {
		return eng;
	}

	public String getKor() {
		return kor;
	}
}
