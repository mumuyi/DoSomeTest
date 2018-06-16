package cn.nuaa.ai.LCS;

import java.util.ArrayList;
import java.util.List;

public class SourceCode implements Comparable<SourceCode>{
	private int id;
	private String name;
	private List<TokenList> codes = new ArrayList<TokenList>();

	public SourceCode() {
	}

	public SourceCode(SourceCode sc) {
		this.setId(sc.getId());
		this.setName(sc.getName());
		List<TokenList> list = new ArrayList<TokenList>();
		if (sc.getCodes() != null) {
			for (TokenList tl : sc.getCodes()) {
				list.add(tl);
			}
		}
		this.setCodes(list);
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

	public List<TokenList> getCodes() {
		return codes;
	}

	public void setCodes(List<TokenList> codes) {
		this.codes = codes;
	}

	@Override
	public int compareTo(SourceCode o) {
		for(int i = 0;i < o.getCodes().size() && i < this.getCodes().size();i++){
			if(o.getCodes().get(i).getTokens().get(0).compareTo(this.getCodes().get(i).getTokens().get(0)) < 0){
				return 1;
			}else if(o.getCodes().get(i).getTokens().get(0).compareTo(this.getCodes().get(i).getTokens().get(0)) > 0){
				return -1;
			}else{
				continue;
			}
		}
		return 0;
	}
}
