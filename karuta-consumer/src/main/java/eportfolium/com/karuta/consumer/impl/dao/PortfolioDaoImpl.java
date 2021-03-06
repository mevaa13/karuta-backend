/* =======================================================
	Copyright 2020 - ePortfolium - Licensed under the
	Educational Community License, Version 2.0 (the "License"); you may
	not use this file except in compliance with the License. You may
	obtain a copy of the License at

	http://www.osedu.org/licenses/ECL-2.0

	Unless required by applicable law or agreed to in writing,
	software distributed under the License is distributed on an "AS IS"
	BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
	or implied. See the License for the specific language governing
	permissions and limitations under the License.
   ======================================================= */

package eportfolium.com.karuta.consumer.impl.dao;
// Generated 17 juin 2019 11:33:18 by Hibernate Tools 5.2.10.Final

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import eportfolium.com.karuta.consumer.contract.dao.CredentialDao;
import eportfolium.com.karuta.consumer.contract.dao.NodeDao;
import eportfolium.com.karuta.consumer.contract.dao.PortfolioDao;
import eportfolium.com.karuta.model.bean.Credential;
import eportfolium.com.karuta.model.bean.Node;
import eportfolium.com.karuta.model.bean.Portfolio;
import eportfolium.com.karuta.model.exception.BusinessException;
import eportfolium.com.karuta.model.exception.DoesNotExistException;
import eportfolium.com.karuta.util.JavaTimeUtil;
import eportfolium.com.karuta.util.PhpUtil;

/**
 * Home object implementation for domain model class Portfolio.
 * 
 * @see dao.Portfolio
 * @author Hibernate Tools
 */
@Repository
public class PortfolioDaoImpl extends AbstractDaoImpl<Portfolio> implements PortfolioDao {

	private static final Log log = LogFactory.getLog(PortfolioDaoImpl.class);

	@Autowired
	private CredentialDao credentialDao;

	@Autowired
	private NodeDao nodeDao;

	public PortfolioDaoImpl() {
		super();
		setCls(Portfolio.class);
	}

	public Portfolio getPortfolio(String portfolioUuid) {
		Portfolio result = null;
		try {
			String sql = "SELECT p FROM Portfolio p";
			sql += " WHERE p.id = :portfolioID ";
			TypedQuery<Portfolio> q = em.createQuery(sql, Portfolio.class);
			q.setParameter("portfolioID", UUID.fromString(portfolioUuid));
			result = q.getSingleResult();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public Node getPortfolioRootNode(String portfolioUuid) {
		Node root_node = null;
		String hql = "SELECT rn FROM Portfolio p";
		hql += " LEFT JOIN p.rootNode as rn";
		hql += " WHERE p.id = :portfolioID ";
		TypedQuery<Node> q = em.createQuery(hql, Node.class);
		q.setParameter("portfolioID", UUID.fromString(portfolioUuid));
		try {
			root_node = q.getSingleResult();
		} catch (NoResultException e) {
			log.error("getPortfolioRootNode failed", e);
		}
		return root_node;
	}

	public Portfolio getPortfolioFromNode(String nodeUuid) {
		Portfolio res = null;

		String sql = "SELECT p FROM Node n";
		sql += " INNER JOIN n.portfolio p";
		sql += " WHERE n.id = :nodeUuid";
		TypedQuery<Portfolio> q = em.createQuery(sql, Portfolio.class);
		q.setParameter("nodeUuid", UUID.fromString(nodeUuid));
		try {
			res = q.getSingleResult();
		} catch (NoResultException e) {
		}
		return res;
	}

	public UUID getPortfolioUuidFromNode(String nodeUuid) {
		return getPortfolioUuidFromNode(UUID.fromString(nodeUuid));
	}

	public UUID getPortfolioUuidFromNode(UUID nodeUuid) {
		UUID res = null;

		String sql = "SELECT p.id FROM Node n";
		sql += " INNER JOIN n.portfolio p";
		sql += " WHERE n.id = :nodeUuid";
		TypedQuery<UUID> q = em.createQuery(sql, UUID.class);
		q.setParameter("nodeUuid", nodeUuid);
		try {
			res = q.getSingleResult();
		} catch (NoResultException e) {
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getPortfolioShared(Long userId) {
		String sql = "SELECT new map(gi.id AS gid, p.id AS portfolio)";
		sql += " FROM GroupUser gu";
		sql += " INNER JOIN gu.id.groupInfo gi";
		sql += " INNER JOIN gi.groupRightInfo gri";
		sql += " INNER JOIN gri.portfolio p";
		sql += " WHERE gu.id.credential.id = :userId";
		Query q = em.createQuery(sql);
		q.setParameter("userId", userId);
		List<Map<String, Object>> result = q.getResultList();
		return result;
	}

	public List<Portfolio> getPortfolios(Long userId, Long substId, Boolean portfolioActive, Boolean portfolioProject) {
		if (PhpUtil.empty(userId) && PhpUtil.empty(substId)) {
			return Arrays.asList();
		}
		TypedQuery<Portfolio> q;
		String sql = "";

		// Ordering by code. A bit hackish but it work as intended
		// Si on est admin, on récupère la liste complete
		if (userId != null && credentialDao.isAdmin(userId)) {
			sql = "SELECT p FROM Portfolio p";
			sql += " INNER JOIN p.rootNode n";
			sql += " INNER JOIN n.resResource r";
			if (portfolioActive)
				sql += " WHERE p.active = 1";
			else
				sql += " WHERE p.active = 0";
			if( portfolioProject != null && portfolioProject )
				sql += " AND n.semantictag LIKE '%karuta-project%' ";

			sql += " ORDER BY r.content";

			q = em.createQuery(sql, Portfolio.class);
			return q.getResultList();
		}

		if (credentialDao.isAdmin(substId)) {
			// If root wants to debug user UI
			substId = 0L;
		}

		// On récupère d'abord les informations dans la table structures
		// Récupération des portfolios :

		// Étape 1 : ceux qui appartiennent à l'utilisateur
		sql = "SELECT p FROM Portfolio p";
		sql += " INNER JOIN FETCH p.rootNnode n";
		sql += " INNER JOIN FETCH n.resResource r";
		sql += " WHERE p.modifUserId = :modifUserId";
		if (portfolioActive)
			sql += " AND p.active = 1 ";
		else
			sql += " AND p.active = 0 ";
		if( portfolioProject != null && portfolioProject )
			sql += " AND n.semantictag LIKE '%karuta-project%' ";

		q = em.createQuery(sql, Portfolio.class);
		q.setParameter("modifUserId", userId);
		Set<Portfolio> portfolios = new HashSet<Portfolio>(q.getResultList());

		// Étape 2 : Ceux qu'il peut modifier, sur lesquels l'utilisateur a reçu des
		// droits spécifiques.
		sql = " SELECT p FROM Portfolio p";
		sql += " INNER JOIN FETCH p.rootNnode n";
		sql += " INNER JOIN FETCH n.resResource r";
		sql += " INNER JOIN p.credential cr";
		sql += " INNER JOIN cr.groups gu";
//			sql = " SELECT gu FROM GroupUser gu";
//			sql+= " INNER JOIN gu.groupInfo gi;
//		    sql+= " INNER JOIN gi.groupRightInfo gri";
//			sql+= " INNER JOIN FETCH gri.portfolio p";
//			sql += " INNER JOIN FETCH p.rootNnode n";
//			sql += " INNER JOIN FETCH n.resResourceTable r";
//			sql += " WHERE gu.id.credential.id = : userId;

		// Étape 2 : portfolios auxquels l'utilisateur standard et celui de substitution
		// peuvent accéder.
		sql += " WHERE (gu.id.credential.id = :userId OR gu.id.credential.id = :substUserId)";

		// check active from substitute too
		if (portfolioActive)
			sql += " AND p.active = 1 ";
		else
			sql += " AND p.active = 0 ";
		if( portfolioProject != null && portfolioProject )
			sql += " AND n.semantictag LIKE '%karuta-project%' ";

		/// Closing top level query and sorting
		sql += " GROUP BY p.id ORDER BY content";

		q = em.createQuery(sql, Portfolio.class);
		q.setParameter("userId", userId);

		if (PhpUtil.empty(substId)) {
			q.setParameter("substUserId", userId);
		} else {
			q.setParameter("substUserId", substId);
		}

		Set<Portfolio> step2 = new HashSet<Portfolio>(q.getResultList());
		portfolios.addAll(step2);
		return new ArrayList<Portfolio>(portfolios);
	}

	public String getPortfolioUuidFromNodeCode(String nodeCode) {
		Portfolio p = getPortfolioFromNodeCode(nodeCode);
		return p != null ? p.getId().toString() : null;
	}

	public Portfolio getPortfolioFromNodeCode(String nodeCode) {
		Portfolio res = null;

		String sql = "SELECT p FROM Node n";
		sql += " INNER JOIN n.portfolio p WITH p.active = 1";
		sql += " WHERE n.asmType = 'asmRoot'";
		sql += " AND n.code = :nodeCode";

		TypedQuery<Portfolio> q = em.createQuery(sql, Portfolio.class);
		q.setParameter("nodeCode", nodeCode);
		try {
			res = q.getSingleResult();
		} catch (NoResultException ex) {
			ex.printStackTrace();
		}
		return res;
	}

	public Long getPortfolioUserId(String portfolioUuid) {
		String sql = "SELECT cr.id FROM Portfolio p";
		sql += " INNER JOIN p.credential cr";
		sql += " WHERE p.id = :portfolioUuid";
		TypedQuery<Long> q = em.createQuery(sql, Long.class);
		q.setParameter("portfolioUuid", UUID.fromString(portfolioUuid));
		Long res = null;
		try {
			res = q.getSingleResult();
		} catch (NoResultException e) {
		}
		return res;
	}

	public int updatePortfolioModelId(String portfolioUuid, String portfolioModelId) {
		int result = 0;
		String sql = "SELECT p FROM Portfolio p";
		sql += " WHERE p.id = :portfolioUuid";
		TypedQuery<Portfolio> q = em.createQuery(sql, Portfolio.class);
		q.setParameter("portfolioUuid", UUID.fromString(portfolioUuid));
		try {
			Portfolio p = q.getSingleResult();
			p.setModelId(UUID.fromString(portfolioModelId));
			merge(p);
		} catch (Exception ex) {
			result = 1;
			ex.printStackTrace();
		}
		return result;
	}

	public UUID getPortfolioModelUuid(String portfolioUuid) {
		UUID modelID = null;
		String sql = "SELECT p.modelId FROM Portfolio p";
		sql += " WHERE p.id = :portfolioUuid";
		TypedQuery<UUID> q = em.createQuery(sql, UUID.class);
		q.setParameter("portfolioUuid", UUID.fromString(portfolioUuid));
		try {
			modelID = q.getSingleResult();
		} catch (Exception e) {
		}
		return modelID;
	}

	public Long getOwner(String portfolioId) {
		Long result = null;
		String query = "SELECT p.modifUserId FROM Portfolio p";
		query += " WHERE p.id = :id";
		TypedQuery<Long> q = em.createQuery(query, Long.class);
		q.setParameter("id", UUID.fromString(portfolioId));
		try {
			result = q.getSingleResult();
		} catch (NoResultException e) {
		}
		return result;
	}

	public boolean isOwner(Long userId, String portfolioUuid) {
		boolean result = false;
		if (!PhpUtil.empty(userId)) {
			String sql = "SELECT n.modifUserId FROM Node n";
			sql += " INNER JOIN n.portfolio p";
			sql += " WHERE n.modifUserId = :userId";
			sql += " AND p.id = :portfolioUuid";
			Query query = em.createQuery(sql);
			query.setParameter("userId", userId);
			query.setParameter("portfolioUuid", UUID.fromString(portfolioUuid));
			try {
				query.getSingleResult();
				result = true;
			} catch (NoResultException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * Change portfolio owner
	 * 
	 * @param portfolioUuid
	 * @param ownerId
	 * @return
	 * @throws Exception
	 */
	public boolean changePortfolioOwner(String portfolioUuid, Long ownerId) {
		boolean retval = false;

		try {
			String sql = "SELECT p FROM Portfolio p";
			sql += " WHERE p.id = :portfolioUuid";
			TypedQuery<Portfolio> query2 = em.createQuery(sql, Portfolio.class);
			query2.setParameter("portfolioUuid", UUID.fromString(portfolioUuid));
			Portfolio p = query2.getSingleResult();
			p.setModifUserId(ownerId);
			Node rootNode = nodeDao.findById(p.getRootNode().getId());
			rootNode.setModifUserId(ownerId);
			merge(p);
			nodeDao.merge(rootNode);
			retval = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return retval;
	}

	public Portfolio changePortfolioConfiguration(String portfolioUuid, Boolean portfolioActive) {
		Portfolio result = null;
		try {
			Portfolio p = findById(UUID.fromString(portfolioUuid));
			p.setActive(BooleanUtils.toInteger(portfolioActive));
			result = merge(p);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	@Override
	public Portfolio add(String rootNodeUuid, String modelId, Long userId, Portfolio porfolio)
			throws BusinessException {
		if (porfolio.getRootNode() != null) {
			throw new IllegalArgumentException();
		}
		if (porfolio.getCredential() != null) {
			throw new IllegalArgumentException();
		}

		if (modelId != null) {
			porfolio.setModelId(UUID.fromString(modelId));
		}

		Node rootNode = null;
		try {
			rootNode = nodeDao.findById(UUID.fromString(rootNodeUuid));
		} catch (Exception e) {
		}
		Credential credential = credentialDao.findById(userId);

		return add(rootNode, credential, porfolio);
	}

	private Portfolio add(final Node rootNode, final Credential credential, final Portfolio portfolio)
			throws BusinessException {
		if (portfolio.getRootNode() != null) {
			throw new IllegalArgumentException();
		}
		if (portfolio.getCredential() != null) {
			throw new IllegalArgumentException();
		}

		if (rootNode != null) {
			Node n = nodeDao.merge(rootNode);
			// Si l'id est différent, cela signifie que le noeud n’existait pas. la
			// sauvegarde ne doit pas en créer un nouveau.
			if (!n.getId().equals(rootNode.getId())) {
				throw new DoesNotExistException(Node.class, rootNode.getId());
			}
			n.addPortfolio(portfolio);
		}

		Credential cr = credentialDao.merge(credential);
		// Si l'id est différent, cela signifie que l'utilisateur n’existait pas, la
		// sauvegarde ne doit pas en créer un nouveau.
		if (!cr.getId().equals(credential.getId())) {
			throw new DoesNotExistException(Credential.class, credential.getId());
		}
		cr.addPortfolio(portfolio);
		portfolio.setModifUserId(cr.getId());
		persist(portfolio);
		return portfolio;
	}

	/**
	 * Check if base portfolio is public
	 */
	public boolean isPublic(String portfolioUuid) {
		boolean val = false;
		String sql = "SELECT p FROM Portfolio p";
		sql += " INNER JOIN p.groupRightInfo gri WITH gri.label='all'";
		sql += " INNER JOIN gri.groupInfo gi";
		sql += " INNER JOIN gi.groupUser gu";
		sql += " INNER JOIN gu.id.credential c WITH c.login='sys_public'";
		sql += " WHERE p.id = :portfolioUuid";
		Query q = em.createQuery(sql);
		q.setParameter("portfolioUuid", UUID.fromString(portfolioUuid));
		try {
			q.getSingleResult();
			val = true;
		} catch (Exception e) {
		}
		return val;
	}

	public void updateTime(String portfolioUuid) throws DoesNotExistException {
		Portfolio p = findById(UUID.fromString(portfolioUuid));
		Date now = JavaTimeUtil.toJavaDate(LocalDateTime.now());
		p.setModifDate(now);
		merge(p);
	}

	public boolean updateTimeByNode(String nodeUuid) {
		boolean hasChanged = false;
		try {
			if (nodeUuid != null) {
				Node n = nodeDao.findById(UUID.fromString(nodeUuid));
				Portfolio p = n.getPortfolio();
				Date now = JavaTimeUtil.toJavaDate(LocalDateTime.now());
				p.setModifDate(now);
				merge(p);
				hasChanged = true;
			}
		} catch (Exception e) {
		}
		return hasChanged;
	}

	/**
	 * Check if there are shared nodes in this portfolio.
	 */
	public boolean hasSharedNodes(String portfolioUuid) {
		boolean result = false;
		final List<Node> nodes = nodeDao.getSharedNodes(portfolioUuid);
		for (Node n : nodes) {
			UUID sharedNode = n.getSharedNodeUuid();
			if (sharedNode != null) {
				result = true;
				break;
			}
		}
		return result;
	}

	/*************************************************************************************************************/
	@Override
	public ResultSet getMysqlPortfolios(Connection con) {
		PreparedStatement st;
		String sql;
		try {
			sql = "SELECT bin2uuid(p.portfolio_id) AS portfolio_id, bin2uuid(p.root_node_uuid) AS root_node_uuid, p.user_id,";
			sql += " bin2uuid(p.model_id) as model_id, p.modif_user_id, p.modif_date, p.active";
			sql += " FROM portfolio p";
			st = con.prepareStatement(sql);
			return st.executeQuery();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public ResultSet getMysqlPortfolioGroupMembers(Connection con) {
		PreparedStatement st;
		String sql;
		try {
			// On récupère d'abord les informations dans la table structures
			sql = "SELECT pg, bin2uuid(pgm.portfolio_id) AS portfolio_id FROM portfolio_group_members pgm";
			st = con.prepareStatement(sql);

			return st.executeQuery();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
