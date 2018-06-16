package cn.nuaa.ai.LCS;

import java.util.ArrayList;
import java.util.List;

public class TokenList implements Comparable<TokenList>{
	private List<String> tokens;
	private int id;
	private String name;

	public TokenList() {
	}

	public TokenList(TokenList tl) {
		this.id = tl.id;
		this.name = tl.name;
		List<String> list = new ArrayList<String>();
		if (tl.getTokens() != null) {
			for (String t : tl.getTokens()) {
				list.add(t);
			}
		}
		this.setTokens(list);
	}

	public List<String> getTokens() {
		return tokens;
	}

	public void setTokens(List<String> tokens) {
		this.tokens = tokens;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int compareTo(TokenList o) {
		for(int i = 0;i < o.getTokens().size() && i < this.getTokens().size();i++){
			if(o.getTokens().get(i).compareTo(this.getTokens().get(i)) < 0){
				return 1;
			}else if(o.getTokens().get(i).compareTo(this.getTokens().get(i)) > 0){
				return -1;
			}else{
				continue;
			}
		}
		return 0;
	}

}
