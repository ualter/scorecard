package br.ujr.scorecard.util;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class MessageManager {
	
	private static Properties messages;
	
	static {
		messages = new Properties();
		 try {
			messages.load(Thread.currentThread().getClass().getResourceAsStream("/messages.properties"));
		} catch (IOException e) {
			throw new RuntimeException("Arquivo messages.properties não encontrado.");
		}
	}
	
	private MessageManager() {
	}
	
	public static String getMessage(MessagesEnum message, Object entity1, Object entity2) {
		return MessageManager.getMessage(message,null,new Object[]{entity1,entity2});
	}
	public static String getMessage(MessagesEnum message, Object[] entities) {
		return MessageManager.getMessage(message,null,entities);
	}
	
	public static String getMessage(MessagesEnum message, String[] stringValues, Object[] entities) {
		String msg = messages.getProperty(message.name());
		
		if ( (stringValues == null || stringValues.length <= 0) && (entities == null || entities.length <= 0)) {
			return msg;
		} else {
			Pattern pStrings        = Pattern.compile("\\{[0-9]+\\}");
			Pattern pEntities       = Pattern.compile("\\{[a-zA-Z]+\\.[a-zA-Z]+\\}");
		
			Matcher mStrings       = pStrings.matcher(msg);
			Matcher mEntities      = pEntities.matcher(msg);
			
			HashMap<String,Integer> indexes = new HashMap<String,Integer>();
			int i = 0;
			while (mStrings.find()) {
				String group = mStrings.group();
				indexes.put(group, i++);
			}
			mStrings.reset();
			
			while (mStrings.find()) {
				String group = mStrings.group();
				String index = group.substring(1, group.length() -1);
				if ( !StringUtils.contains(msg,group) ) {
					throw new RuntimeException("Mensagem mal formada. Não foi enviado valor para o parâmetro " + group);
				}
				msg = msg.replaceAll(substituirChaves(group), stringValues[indexes.get(group)].toString());
			}
			
			while (mEntities.find()) {
				String   group  = mEntities.group();
				String[] names  = group.substring(1,group.length() - 1).split("\\.");
				String   entity = names[names.length- 2];
				String   field  = names[names.length -1];
				field           = field.substring(0,1).toUpperCase() + field.substring(1);
				try {
					Object entityObj = findEntity(entity,entities); 
					Method method    = entityObj.getClass().getMethod("get" + field, null);
					Object result    = method.invoke(entityObj, null);
					msg = msg.replaceAll(substituirChaves(group), result.toString());
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			return msg;
		}
	}
	
	private static Object findEntity(String entity, Object[] entities) {
		for (int i = 0; i < entities.length; i++) {
			
			String entityName = entities[i].getClass().getName();
			entityName        = entityName.substring(entityName.lastIndexOf(".") + 1);
			
			if ( entity.equalsIgnoreCase(entityName) ) {
				return entities[i];
			}
		}
		throw new RuntimeException("Mensagem mal formada. Não foi encontrada a entidade " + entity + " na mensagem.");
	}
	private static String substituirChaves(String s) {
		s = s.replaceAll("\\{", "\\\\\\{");
		s = s.replaceAll("\\}", "\\\\\\}");
		return s;
	}
	
	public static void main(String[] args) {
		String s = "{0}";
		System.out.println(s.replaceAll("\\{", "\\\\{"));
		/*String m = "TituloFrameCheque = Cheques - {12} - {cc.ContaCorrente.name} - {0} {}";
		
		Pattern pStrings  = Pattern.compile("\\{[0-9]+\\}");
		Pattern pEntities = Pattern.compile("\\{[a-zA-Z\\.]+\\}");
		Pattern pIndexes  = Pattern.compile("\\{[a-zA-Z\\.[0-9]]+\\}");
		
		Matcher mIndexed  = pIndexes.matcher(m);
		Matcher mStrings   = pStrings.matcher(m);
		Matcher mEntities  = pEntities.matcher(m);
		
		while (mStrings.find()) {
			String group = mStrings.group();
			String index = group.substring(1, group.length() -1);
			System.out.print(index + ",");
		}
		System.out.println("");
		
		while (mEntities.find()) {
			String   group  = mEntities.group();
			String[] names  = group.substring(1,group.length() - 1).split("\\.");
			String   entity = names[names.length- 2];
			String   field  = names[names.length -1];
			field           = field.substring(0,1).toUpperCase() + field.substring(1);
			
			System.out.print(entity + "-->" + field + ",");
		}
		System.out.println("");
		
		HashMap indexes = new HashMap();
		int i =0;
		while (mIndexed.find()) {
			String group = mIndexed.group();
			indexes.put(group, ++i);
		}
		for (Iterator iterator = indexes.keySet().iterator(); iterator.hasNext();) {
			String element = (String) iterator.next();
			System.out.print(element + "=" + indexes.get(element) + ", ");
		}*/
	}

}
