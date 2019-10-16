package eportfolium.com.karuta.consumer.impl.dao;
// Generated 17 juin 2019 11:33:18 by Hibernate Tools 5.2.10.Final

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import eportfolium.com.karuta.consumer.contract.dao.NodeDao;
import eportfolium.com.karuta.model.bean.Node;
import eportfolium.com.karuta.model.bean.Portfolio;
import eportfolium.com.karuta.model.exception.DoesNotExistException;
import eportfolium.com.karuta.util.PhpUtil;

/**
 * Home object implementation for domain model class Node.
 * 
 * @see dao.Node
 * @author Hibernate Tools
 */
@Repository
public class NodeDaoImpl extends AbstractDaoImpl<Node> implements NodeDao {

	public NodeDaoImpl() {
		super();
		setCls(Node.class);

	}

	public String getPortfolioIdFromNode(String nodeUuid) {
		String result = null;
		String hql = "SELECT p.id FROM Node n";
		hql += " LEFT JOIN FETCH n.portfolio p";
		hql = " WHERE n.id = :nodeUUID";

		TypedQuery<String> q = em.createQuery(hql, String.class);
		q.setParameter("nodeUUID", nodeUuid);
		try {
			result = q.getSingleResult();
		} catch (NoResultException e) {
		}
		return result;
	}

	public Node getNodeBySemanticTag(UUID nodeUuid, String semantictag) {
		Node n = null;
		String sql = "SELECT n FROM Node";
		sql += " WHERE n.semantictag = :semantictag";
		sql += " AND n.id = :id";
		TypedQuery<Node> q = em.createQuery(sql, Node.class);
		q.setParameter("semantictag", semantictag);
		q.setParameter("id", nodeUuid);
		try {
			n = q.getSingleResult();
		} catch (NoResultException e) {
			e.printStackTrace();
		}
		return n;
	}

	public Node getNodeBySemanticTag(String nodeUuid, String semantictag) {
		return getNodeBySemanticTag(UUID.fromString(nodeUuid), semantictag);
	}

	public List<Node> getNodesBySemanticTag(UUID portfolioUuid, String semantictag) {
		String regexSemantictag = "%" + semantictag + "%";
		String sql = "SELECT n FROM Node n";
		sql += " INNER JOIN n.portfolio p WITH p.id = :portfolioUuid";
		sql += " WHERE n.semantictag LIKE :semantictag";
		sql += " ORDER BY n.code, n.nodeOrder";
		TypedQuery<Node> q = em.createQuery(sql, Node.class);
		q.setParameter("portfolioUuid", portfolioUuid);
		q.setParameter("semantictag", regexSemantictag);
		return q.getResultList();
	}

	public List<Node> getNodesBySemanticTag(String portfolioUuid, String semantictag) {
		return getNodesBySemanticTag(UUID.fromString(portfolioUuid), semantictag);
	}

	public Integer getNodeOrderByNodeUuid(String nodeUuid) {
		Integer res = null;
		try {
			String sql = "SELECT n.nodeOrder FROM Node n";
			sql += " WHERE n.id = :nodeUuid";
			TypedQuery<Integer> q = em.createQuery(sql, Integer.class);
			q.setParameter("nodeUuid", nodeUuid);
			res = q.getSingleResult();
		} catch (NoResultException e) {
			e.printStackTrace();
		}
		return res;

	}

	public int create(String nodeUuid, String nodeParentUuid, String nodeChildrenUuid, String asmType, String xsiType,
			boolean sharedRes, boolean sharedNode, boolean sharedNodeRes, String sharedResUuid, String sharedNodeUuid,
			String sharedNodeResUuid, String metadata, String metadataWad, String metadataEpm, String semtag,
			String semanticTag, String label, String code, String descr, String format, int order, Long modifUserId,
			String portfolioUuid) {
		Node node = new Node();
		try {
			node = findById(nodeUuid);
		} catch (Exception ex) {

		}
		node.setParentNode(nodeParentUuid);
		if (nodeChildrenUuid != null) {
			node.setChildrenStr(nodeChildrenUuid);
		}
		node.setNodeOrder(order);
		node.setAsmType(asmType);
		node.setXsiType(xsiType);
		node.setSharedRes(sharedRes);
		node.setSharedNode(sharedNode);
		node.setSharedNodeRes(sharedNodeRes);
		node.setSharedResUuid(sharedResUuid);
		node.setSharedNodeUuid(sharedNodeUuid);
		node.setSharedNodeResUuid(sharedNodeResUuid);
		node.setMetadata(metadata);
		node.setMetadataWad(metadataWad);
		node.setMetadataEpm(metadataEpm);
		node.setSemtag(semtag);
		node.setSemantictag(semanticTag);
		node.setLabel(label);
		node.setCode(code);
		node.setDescr(descr);
		node.setFormat(format);
		node.setModifUserId(modifUserId);
		node.setPortfolio(new Portfolio(portfolioUuid));
		try {
			merge(node);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 1;
		}
		return 0;
	}

	public int update(String nodeUuid, String asmType, String xsiType, String semantictag, String label, String code,
			String descr, String format, String metadata, String metadataWad, String metadataEpm, boolean sharedRes,
			boolean sharedNode, boolean sharedNodeRes, Long modifUserId) {
		Node node = null;
		try {
			node = findById(UUID.fromString(nodeUuid));
		} catch (Exception ex) {
			return 1;
		}
		node.setAsmType(asmType);
		node.setXsiType(xsiType);
		node.setSemantictag(semantictag);
		node.setLabel(label);
		node.setCode(code);
		node.setDescr(descr);
		node.setMetadata(metadata);
		node.setMetadataWad(metadataWad);
		node.setMetadataEpm(metadataEpm);
		node.setSharedRes(sharedRes);
		node.setSharedNode(sharedNode);
		node.setSharedNodeRes(sharedNodeRes);
		node.setModifUserId(modifUserId);
		try {
			merge(node);
		} catch (Exception e) {
			return 1;
		}

		return 0;
	}

	public int updateNodeOrder(String nodeUuid, int order) {
		int result = 0;
		try {
			Node n = findById(UUID.fromString(nodeUuid));
			n.setNodeOrder(order);
			merge(n);
		} catch (Exception e) {
			result = 1;
		}
		return result;
	}

	public int updateNodeCode(String nodeUuid, String code) {
		int result = 0;
		try {
			Node n = findById(UUID.fromString(nodeUuid));
			n.setCode(code);
			merge(n);
		} catch (Exception e) {
			result = 1;
		}
		return result;
	}

	public Node getParentNode(String portfolioUuid, String semtag_parent, String code_parent) throws Exception {
		Node node = null;
		String sql = "SELECT n FROM Node n";
		sql += " WHERE n.portfolio.id = :Uuid";
		sql += " AND n.metadata LIKE :metadata ";
		sql += " AND n.code = :code";
		TypedQuery<Node> q = em.createQuery(sql, Node.class);
		q.setParameter("Uuid", portfolioUuid);
		q.setParameter("metadata", "%semantictag=%" + semtag_parent + "%");
		q.setParameter("code", code_parent);
		try {
			node = q.getSingleResult();
		} catch (NoResultException e) {
			e.printStackTrace();
		}
		return node;
	}

	public Node getParentNode(String parentUuid, String semantictag) {
		Node node = null;
		String sql = "SELECT n FROM Node n";
		sql += " AND n.semantictag LIKE :semantictag";
		sql += " AND n.parent.id = :parentUuid";
		TypedQuery<Node> q = em.createQuery(sql, Node.class);
		q.setParameter("parentUuid", parentUuid);
		q.setParameter("semantictag", "%" + semantictag + "%");
		try {
			node = q.getSingleResult();
		} catch (NoResultException e) {
			e.printStackTrace();
		}
		return node;
	}

	public Node getParentNodeByNodeUuid(String nodeUuid) {
		Node result = null;
		String sql = "SELECT pn FROM Node n";
		sql += " INNER JOIN FETCH n.parentNode pn";
		sql += " WHERE n.id = :nodeUuid";
		TypedQuery<Node> q = em.createQuery(sql, Node.class);
		q.setParameter("nodeUuid", nodeUuid);
		try {
			result = q.getSingleResult();
		} catch (NoResultException ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public String getParentNodeUuidByNodeUuid(String nodeUuid) {
		Node res = getParentNodeByNodeUuid(nodeUuid);
		return res != null ? res.getId() : null;
	}

	public List<Node> getNodesWithResources(String portfolioUuid) {
		String sql = "SELECT n FROM Node n";
		// Récupération des ressources
		sql += " LEFT JOIN FETCH n.resource r1";
		sql += " LEFT JOIN FETCH n.resResource r2";
		sql += " LEFT JOIN FETCH n.contextResource r3";
		sql += " WHERE n.portfolio.id = :portfolioUuid";
		TypedQuery<Node> q = em.createQuery(sql, Node.class);
		q.setParameter("portfolioUuid", portfolioUuid);
		return q.getResultList();
	}

	public List<Node> getNodes(String portfolioUuid) {
		return em.createQuery("SELECT n FROM Node n WHERE n.portfolio.id = :portfolioUuid", Node.class)
				.setParameter("portfolioUuid", portfolioUuid).getResultList();
	}

	public List<Node> getNodes(Collection<String> nodeIds) {
		String sql = "SELECT n FROM node n"; // Going back to original table,
		sql += " LEFT JOIN FETCH n.resource r1";// Récupération des données
		sql += " LEFT JOIN FETCH n.resResource r2"; // Récupération des données
		sql += " LEFT JOIN FETCH n.contextResource r3"; // Récupération des données
		sql += " WHERE n.id IN (" + PhpUtil.implode(",", nodeIds) + ")"; // Selon notre filtrage, prendre les noeuds
		TypedQuery<Node> q = em.createQuery(sql, Node.class);
		return q.getResultList();
	}

	public boolean isPublic(UUID nodeUuid) {
		boolean val = false;
		if (nodeUuid != null) {
			String sql = "SELECT n FROM Node n";
			sql += " INNER JOIN n.portfolio p";
			sql += " INNER JOIN p.groupRightInfo gri WITH gri.label='all'";
			sql += " INNER JOIN gri.groupInfo gi";
			sql += " INNER JOIN gi.groupUser gu";
			sql += " INNER JOIN gu.id.credential c WITH c.login='sys_public'";
			sql += " WHERE n.id = :nodeUuid";
			Query q = em.createQuery(sql);
			q.setParameter("nodeUuid", nodeUuid);
			try {
				q.getSingleResult();
				val = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return val;
	}

	public boolean isPublic(String nodeUuid) {
		return isPublic(UUID.fromString(nodeUuid));
	}

	/**
	 * Check if same code allowed with nodes in different portfolio, and not root
	 * node
	 */
	public boolean isCodeExist(char[] code, UUID nodeuuid) {
		boolean response = false;
		String sql = "SELECT p.id FROM Node n";
		sql += " INNER JOIN n.portfolio p";
		sql += " WHERE n.asmType = :asmType";
		sql += " AND n.code = :code";
		if (nodeuuid != null) {
			sql += " AND n.id != :nodeuuid";
			sql += " AND p.id = (SELECT n.portfolio.id FROM Node n WHERE n.id = :nodeuuid)";
		}
		TypedQuery<UUID> q = em.createQuery(sql, UUID.class);
		q.setParameter("asmType", "asmRoot");
		q.setParameter("code", code);
		if (nodeuuid != null) {
			q.setParameter("nodeuuid", nodeuuid);
		}
		try {
			q.getSingleResult();
			response = true;
		} catch (NoResultException ex) {

		} catch (NonUniqueResultException ex) {
			response = true;
		}
		return response;
	}

	/**
	 * Check if same code allowed with nodes in different portfolio, and not root
	 * node
	 *
	 */
	public boolean isCodeExist(String code, String nodeuuid) {
		return isCodeExist(code.toCharArray(), UUID.fromString(nodeuuid));
	}

	public List<Node> getFirstLevelChildren(String parentNodeUuid) {
		String sql = new String();
		sql += "SELECT n FROM Node n";
		sql += " WHERE n.parentNode.id = :nodeUuid";
		sql += " ORDER by n.nodeOrder ASC";

		TypedQuery<Node> q = em.createQuery(sql, Node.class);
		q.setParameter("nodeUuid", parentNodeUuid);
		List<Node> l = q.getResultList();
		return l;
	}

	/**
	 * Maj. le noeud en base de données.
	 * 
	 * @param nodeUuid
	 * @return
	 */
	@Override
	public int updateNode(String nodeUuid) {
		int status = 0;

		try {
			List<Node> nodes = getFirstLevelChildren(nodeUuid);
			/// Re-numérote les noeuds (on commence à 0)
			for (int i = 0; i < nodes.size(); i++) {
				nodes.get(i).setNodeOrder(i);
				merge(nodes.get(i));
			}

			String sql = "SELECT COALESCE(n.sharedNodeUuid, n.id) AS value FROM Node n";
			sql += " INNER JOIN n.parentNode pNode WITH pNode.id = :nodeUuid";
			sql += " GROUP BY pNode.id";
			sql += " ORDER BY n.nodeOrder";

			TypedQuery<UUID> q = em.createQuery(sql, UUID.class);
			q.setParameter("nodeUuid", UUID.fromString(nodeUuid));
			List<UUID> uuids = q.getResultList();
			List<String> uuidsStr = new ArrayList<>(uuids.size());
			for (UUID uuid : uuids) {
				uuidsStr.add(uuid.toString());
			}
			String children = StringUtils.join(uuidsStr, ",");
			Node n = findById(UUID.fromString(nodeUuid));
			/// Met à jour les enfants
			n.setChildrenStr(children);
			merge(n);
			status = 0;
		} catch (Exception e) {
			e.printStackTrace();
			status = 1;
		}
		return status;
	}

	/**
	 * Fetch metadata
	 */
	public String getMetadataWad(String nodeUuid) {
		return getMetadataWad(UUID.fromString(nodeUuid));
	}

	public String getMetadataWad(UUID nodeUuid) {
		String metadata = null;
		String sql = "SELECT n.metadataWad FROM Node n";
		sql += " WHERE n.id = :nodeUuid";
		TypedQuery<String> q = em.createQuery(sql, String.class);
		q.setParameter("nodeUuid", nodeUuid);
		try {
			metadata = q.getSingleResult();
		} catch (NoResultException e) {
		}
		return metadata;
	}

	/**
	 * Pour retrouver les enfants du noeud et affecter les droits
	 */
	public List<Node> getChildren(String nodeUuid) throws DoesNotExistException {

		final Map<Integer, List<Node>> children = new HashMap<Integer, List<Node>>();
		final Map<Integer, List<String>> childrenIds = new HashMap<Integer, List<String>>();
		final Node n = findById(UUID.fromString(nodeUuid));

		int level = 0;
		int added = 1;

		childrenIds.put(level, Arrays.asList(n.getId().toString()));
		children.put(level, Arrays.asList(n));
		TypedQuery<Node> q = null;
		List<String> nodeIds = null;
		String sql = null;

		/// On boucle pour récupérer les noeuds par niveau.
		while (added != 0) {
			sql = "SELECT n FROM Node n";
			sql += " WHERE n.parentNode.id IN (" + PhpUtil.implode(",", childrenIds.get(level)) + ")";
			sql += " ORDER by n.nodeOrder ASC";
			q = em.createQuery(sql, Node.class);
			List<Node> nodes = q.getResultList();
			if (CollectionUtils.isNotEmpty(nodes)) {
				level = level + 1; // on descend d'un niveau.
				children.put(level, nodes);
				nodeIds = new ArrayList<String>(nodes.size());
				for (Node node : nodes) {
					nodeIds.add(node.getId().toString());
				}
				childrenIds.put(level, nodeIds);
			} else {
				added = 0; // On s'arrete lorsqu'aucun enfant n'a été trouvé.
			}
		}

		final Set<Node> nodes = new LinkedHashSet<Node>();
		for (List<Node> tmpList : children.values()) {
			nodes.addAll(tmpList);
		}
		return new ArrayList<Node>(nodes);
	}

	public Node getNodeBySemtag(String semtag) {
		Node n = null;
		String sql = "SELECT n FROM Node n";
		sql += " WHERE n.semantictag LIKE \"" + semtag + "\"";
		TypedQuery<Node> q = em.createQuery(sql, Node.class);
		List<Node> tmpList = q.getResultList();
		if (CollectionUtils.isNotEmpty(tmpList)) {
			n = tmpList.get(0);
		}
		return n;
	}

	public String getNodeUuidBySemtag(String semtag, String parentUuid) throws DoesNotExistException {
		Node result = null;
		final Map<Integer, List<Node>> children = new HashMap<Integer, List<Node>>();
		final Map<Integer, List<String>> childrenIds = new HashMap<Integer, List<String>>();

		int level = 0;
		int added = 1;

		Node n = findById(UUID.fromString(parentUuid));

		childrenIds.put(level, Arrays.asList(n.getId().toString()));
		children.put(level, Arrays.asList(n));
		TypedQuery<Node> q = null;
		List<String> nodeIds = null;
		String sql = null;

		// On boucle pour récupérer les noeuds par niveau.
		while (added != 0) {
			sql = "SELECT n FROM Node n";
			sql += " WHERE n.parentNode.id IN (" + PhpUtil.implode(",", childrenIds.get(level)) + ")";
			sql += " ORDER by n.nodeOrder ASC";
			q = em.createQuery(sql, Node.class);
			List<Node> nodes = q.getResultList();
			if (CollectionUtils.isNotEmpty(nodes)) {
				level = level + 1; // on descend d'un niveau.
				children.put(level, nodes);
				nodeIds = new ArrayList<String>(nodes.size());
				for (Node node : nodes) {
					nodeIds.add(node.getId().toString());
				}
				childrenIds.put(level, nodeIds);
			} else {
				added = 0; // On s'arrete lorsqu'aucun enfant n'a été trouvé.
			}
		}

		for (List<Node> tmpList : children.values()) {
			for (Node tmp : tmpList) {
				if (StringUtils.equals(tmp.getSemantictag(), semtag)) {
					result = tmp;
					break;
				}
			}
			if (result != null) {
				break;
			}
		}
		return result != null ? result.getId().toString() : null;
	}

	public Integer getNodeNextOrderChildren(String nodeUuid) {
		Integer res = Integer.valueOf(0);
		String sql = "SELECT COUNT(n) FROM Node n";
		sql += " WHERE n.parentNode.id  = :parentNodeUuid";
		sql += " GROUP BY n.parentNode.id";
		TypedQuery<Number> q = em.createQuery(sql, Number.class);
		q.setParameter("parentNodeUuid", UUID.fromString(nodeUuid));
		try {
			res = q.getSingleResult().intValue();
		} catch (Exception ex) {
		}
		return res;
	}

	public String getNodeUuidByPortfolioModelAndSemanticTag(String portfolioModelId, String semanticTag) {
		String res = null;
		String sql = "SELECT n.id FROM Node n";
		sql += " INNER JOIN n.portfolio p WITH p.modelId = :modelId";
		sql += " WHERE n.semanticTag = :semanticTag";
		TypedQuery<String> query = em.createQuery(sql, String.class);
		query.setParameter("modelId", portfolioModelId);
		query.setParameter("semanticTag", semanticTag);
		try {
			res = query.getSingleResult();
		} catch (NoResultException ex) {
			ex.printStackTrace();
		}
		return res;
	}

	/**
	 * Récupère les noeuds partagés dans un portfolio.
	 */
	public List<Node> getSharedNodes(String portfolioUuid) {
		String sql = "SELECT n FROM node n";
		sql += " WHERE n.portfolio.id = :portfolioUuid";
		sql += " AND n.sharedNode = TRUE";
		TypedQuery<Node> q = em.createQuery(sql, Node.class);
		q.setParameter("portfolioUuid", portfolioUuid);
		return q.getResultList();
	}

	public List<Node> getNodesByOrder(String nodeUuid, int order) {
		String sql = "SELECT n FROM Node n";
		sql += " WHERE n.nodeOrder IN (" + (order - 1) + "," + order + ")";
		sql += " AND n.parentNode.id = :nodeUuid";
		TypedQuery<Node> q = em.createQuery(sql, Node.class);
		q.setParameter("nodeUuid", nodeUuid);
		return q.getResultList();
	}

	@Override
	public ResultSet getMysqlNodes(Connection con) {
		PreparedStatement st;
		String sql;
		try {
			sql = "SELECT bin2uuid(node_uuid) as node_uuid, bin2uuid(node_parent_uuid) as node_parent_uuid, node_children_uuid as node_children_uuid,";
			sql += " node_order, metadata, metadata_wad, metadata_epm, bin2uuid(res_node_uuid) as res_node_uuid, bin2uuid(res_res_node_uuid) as res_res_node_uuid,";
			sql += " bin2uuid(res_context_node_uuid) as res_context_node_uuid, shared_res, shared_node, asm_type, xsi_type, semtag, label, code, descr, format, modif_user_id, modif_date, bin2uuid(portfolio_id) as portfolio_id";
			sql += " FROM node";
			st = con.prepareStatement(sql);
			return st.executeQuery();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public ResultSet getMysqlChildrenNodes(Connection con, String parentNodeUuid) {
		PreparedStatement st;
		String sql;
		try {
			sql = "SELECT bin2uuid(node_uuid) as node_uuid, bin2uuid(node_parent_uuid) as node_parent_uuid, node_children_uuid as node_children_uuid,";
			sql += " node_order, metadata, metadata_wad, metadata_epm, bin2uuid(res_node_uuid) as res_node_uuid, bin2uuid(res_res_node_uuid) as res_res_node_uuid,";
			sql += " bin2uuid(res_context_node_uuid) as res_context_node_uuid, shared_res, shared_node, asm_type, xsi_type, semtag, label, code, descr, format, modif_user_id, modif_date, bin2uuid(portfolio_id) as portfolio_id";
			sql += " FROM node";
			sql += " WHERE node_parent_uuid = uuid2bin(?)";
			st = con.prepareStatement(sql);
			st.setString(1, parentNodeUuid);
			return st.executeQuery();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public ResultSet getMysqlRootNodes(Connection con) {
		PreparedStatement st;
		String sql;
		try {
			sql = "SELECT bin2uuid(node_uuid) as node_uuid, bin2uuid(node_parent_uuid) as node_parent_uuid, node_children_uuid as node_children_uuid,";
			sql += " node_order, metadata, metadata_wad, metadata_epm, bin2uuid(res_node_uuid) as res_node_uuid, bin2uuid(res_res_node_uuid) as res_res_node_uuid,";
			sql += " bin2uuid(res_context_node_uuid) as res_context_node_uuid, shared_res, shared_node, asm_type, xsi_type, semtag, label, code, descr, format, modif_user_id, modif_date, bin2uuid(portfolio_id) as portfolio_id";
			sql += " FROM node";
			sql += " WHERE node_parent_uuid IS NULL";
			st = con.prepareStatement(sql);
			return st.executeQuery();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public ResultSet getMysqlRootNode(Connection con, String portfolioUuid) {
		PreparedStatement st;
		String sql;
		try {
			sql = "SELECT bin2uuid(node_uuid) as node_uuid, bin2uuid(node_parent_uuid) as node_parent_uuid, node_children_uuid as node_children_uuid,";
			sql += " node_order, metadata, metadata_wad, metadata_epm, bin2uuid(res_node_uuid) as res_node_uuid, bin2uuid(res_res_node_uuid) as res_res_node_uuid,";
			sql += " bin2uuid(res_context_node_uuid) as res_context_node_uuid, shared_res, shared_node, asm_type, xsi_type, semtag, label, code, descr, format, modif_user_id, modif_date, bin2uuid(portfolio_id) as portfolio_id";
			sql += " FROM node";
			sql += " WHERE portfolio_id = uuid2bin(?)";
			st = con.prepareStatement(sql);
			st.setString(1, portfolioUuid);
			return st.executeQuery();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public ResultSet getMysqlNode(Connection c, String nodeUuid) {
		PreparedStatement st;
		String sql;

		try {
			sql = "SELECT bin2uuid(node_uuid) as node_uuid, bin2uuid(node_parent_uuid) as node_parent_uuid,  node_children_uuid as node_children_uuid, node_order, metadata, metadata_wad, metadata_epm, bin2uuid(res_node_uuid) as res_node_uuid,  bin2uuid(res_res_node_uuid) as res_res_node_uuid,  bin2uuid(res_context_node_uuid) as res_context_node_uuid, shared_res, shared_node, asm_type, xsi_type, semtag, label, code, descr, format, modif_user_id, modif_date,  bin2uuid(portfolio_id) as portfolio_id FROM node WHERE node_uuid = uuid2bin(?) ";
			st = c.prepareStatement(sql);
			st.setString(1, nodeUuid);

			return st.executeQuery();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
