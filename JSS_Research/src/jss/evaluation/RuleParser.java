package jss.evaluation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jss.ActionHandler;
import jss.evolution.node.INode;

import org.reflections.Reflections;

/**
 * TODO javadoc.
 *
 * @author John Park
 *
 */
public class RuleParser {

	private static final String BASE_NODE_DIR = "jss.evolution";
	
	private Map<String, NodeChildrenNumPair> nodeMap = new HashMap<String, NodeChildrenNumPair>();
	
	public RuleParser() {
		// Load up all of the nodes that will be used for the parsing.
		loadNodes();
	}
	
	// TODO docs.
	private void loadNodes() {
		try {
			Reflections reflections = new Reflections(BASE_NODE_DIR);
			
			Set<Class<? extends INode>> subTypes = reflections.getSubTypesOf(INode.class);
	
			for (Class<? extends INode> subType : subTypes) {
				INode obj = subType.newInstance();
				
				NodeChildrenNumPair pair = new NodeChildrenNumPair();
				pair.nodeClass = subType;
				pair.childrenNum = obj.getChildrenNum();
				
				nodeMap.put(obj.toString(), pair);
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	// TODO docs.
	public INode getRuleFromString(String ruleString) {
		return new SubParser(ruleString).parse();
	}
	
	private class SubParser {
		private String input;
		private int index = 0;
		
		public SubParser(String input) {
			this.input = input;
		}
		
		public INode parse() {
			ignoreWhitespace();
			
			if (input.charAt(index) == '(') {
				// Parse the non-terminal node.
				match("(");
				
				String token = readToken();
				if (!nodeMap.containsKey(token)) {
					throw new RuntimeException("You done goofed again from RuleParser");
				}
				
				NodeChildrenNumPair pair = nodeMap.get(token);
				INode node = generateNewNode(pair);
				
				// Parse the child nodes.
				INode[] children = new INode[pair.childrenNum];
				for (int i = 0; i < pair.childrenNum; i++) {
					children[i] = parse();
				}
				
				
				match(")");
				
				return node;
			} else {
				// Parse the terminal node.
				String token = readToken();
				if (!nodeMap.containsKey(token)) {
					throw new RuntimeException("You done goofed again from RuleParser");
				}
				
				NodeChildrenNumPair pair = nodeMap.get(token);
				return generateNewNode(pair);
			}
			
		}
		
		private INode generateNewNode(NodeChildrenNumPair pair) {
			try {
				return pair.nodeClass.newInstance();
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		
		private void ignoreWhitespace() {
			while (index < input.length() && Character.isWhitespace(input.charAt(index))) {
				index++;
			}
		}
		
		private String readToken() {
			int startIndex = index;
			while (index < input.length() && !Character.isWhitespace(input.charAt(index))) {
				index++;
			}
			return input.substring(startIndex, index);
		}
		
		private void match(String match) {
			if (!input.substring(index).startsWith(match)) {
				throw new RuntimeException("You done goofed from RuleParser");
			}
			index += match.length();
		}
	}
	
	private class NodeChildrenNumPair {
		Class<? extends INode> nodeClass;
		int childrenNum;
	}

}
