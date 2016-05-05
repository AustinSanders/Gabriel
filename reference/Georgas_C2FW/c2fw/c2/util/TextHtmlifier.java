package c2.util;

public class TextHtmlifier{
	
	public static String htmlify(String s){
		StringBuffer sb = new StringBuffer();
		char[] cs = s.toCharArray();
		for(int i = 0; i < cs.length; i++){
			switch(cs[i]){
			case '<':
				sb.append("&lt;");
				break;
			case '>':
				sb.append("&gt;");
				break;
			case '&':
				sb.append("&amp;");
				break;
			case ' ':
				sb.append("&nbsp;");
				break;
			case '"':
				sb.append("&quot;");
				break;
			case '\n':
				sb.append("<br>");
				break;
			case '\t':
				sb.append("&nbsp;&nbsp;&nbsp;&nbsp;");
				break;
			default:
				sb.append(cs[i]);
			}
		}
		return sb.toString();
	}

}
