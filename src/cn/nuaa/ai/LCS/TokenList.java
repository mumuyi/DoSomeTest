package cn.nuaa.ai.LCS;

import java.util.List;

public class TokenList {
	private List<String> tokens;

	public TokenList(){}

	public TokenList(TokenList tl){
		this.tokens = tl.tokens;
	}
	
	public List<String> getTokens() {
		return tokens;
	}

	public void setTokens(List<String> tokens) {
		this.tokens = tokens;
	}

}
