package eportfolium.com.karuta.model.bean;
// Generated 13 juin 2019 19:14:13 by Hibernate Tools 5.2.10.Final

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.search.annotations.Indexed;

/** ResourceTable generated by hbm2java */
@Indexed
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "resource_table")
public class ResourceTable implements Serializable {

	private static final long serialVersionUID = 3994535580465308742L;

	private String id;
	private String xsiType;
	private String content;
	private Credential credential;
	private Long modifUserId;
	private Date modifDate;

	private Node node;
	private Node resNode;
	private Node contextNode;

	public ResourceTable() {
	}

	public ResourceTable(ResourceTable resource) {
		this.xsiType = resource.xsiType != null ? new String(resource.xsiType) : null;
		this.content = resource.content != null ? new String(resource.content) : null;
		this.credential = resource.credential != null ? new Credential(resource.credential.getId()) : null;
		this.modifUserId = resource.modifUserId != null ? Long.valueOf(resource.modifUserId) : null;
		this.modifDate = resource.modifDate != null ? new Date(resource.modifDate.getTime()) : null;
	}

	public ResourceTable(String id) {
		this.id = id;
	}

	public ResourceTable(String nodeUuid, Long modifUserId) {
		this.id = nodeUuid;
		this.modifUserId = modifUserId;
	}

	public ResourceTable(String nodeUuid, String xsiType, String content, Credential credential, Long modifUserId,
			Date modifDate) {
		this.id = nodeUuid;
		this.xsiType = xsiType;
		this.content = content;
		this.credential = credential;
		this.modifUserId = modifUserId;
		this.modifDate = modifDate;
	}

	@Id
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@GeneratedValue(generator = "uuid2")
	@Column(name = "node_uuid", unique = true, nullable = false, length = 36)
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "xsi_type", length = 50)
	public String getXsiType() {
		return this.xsiType;
	}

	public void setXsiType(String xsiType) {
		this.xsiType = xsiType;
	}

	@Lob
	@Column(name = "content")
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	public Credential getCredential() {
		return this.credential;
	}

	public void setCredential(Credential credential) {
		this.credential = credential;
	}

	@Column(name = "modif_user_id", nullable = false)
	public Long getModifUserId() {
		return this.modifUserId;
	}

	public void setModifUserId(Long modifUserId) {
		this.modifUserId = modifUserId;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modif_date", length = 19)
	public Date getModifDate() {
		return this.modifDate;
	}

	public void setModifDate(Date modifDate) {
		this.modifDate = modifDate;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "resource", cascade = CascadeType.ALL)
	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "resResource", cascade = CascadeType.ALL)
	public Node getResNode() {
		return resNode;
	}

	public void setResNode(Node resNode) {
		this.resNode = resNode;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "contextResource", cascade = CascadeType.ALL)
	public Node getContextNode() {
		return contextNode;
	}

	public void setContextNode(Node contextNode) {
		this.contextNode = contextNode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResourceTable other = (ResourceTable) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
