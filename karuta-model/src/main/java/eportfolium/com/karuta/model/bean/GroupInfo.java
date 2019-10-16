package eportfolium.com.karuta.model.bean;
// Generated 13 juin 2019 19:14:13 by Hibernate Tools 5.2.10.Final

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.search.annotations.Indexed;

/**
 * GroupInfo generated by hbm2java
 */
@Indexed
@Entity
@Table(name = "group_info")
//@TableGenerator(name = "group_info_table", initialValue = 2, allocationSize = 2)
public class GroupInfo implements Serializable {

	private static final long serialVersionUID = -4066550096842378284L;

	private Long id;
	private GroupRightInfo groupRightInfo;
	private long owner;
	private String label;
	private Set<GroupUser> groupUser = new HashSet<GroupUser>(0);

	public GroupInfo() {
	}

	public GroupInfo(Long groupId) {
		this.id = groupId;
	}

	public GroupInfo(Long id, long owner, String label) {
		this.id = id;
		this.owner = owner;
		this.label = label;
	}

	public GroupInfo(Long id, GroupRightInfo groupRightInfo, long owner, String label) {
		this.id = id;
		this.groupRightInfo = groupRightInfo;
		this.owner = owner;
		this.label = label;
	}

//	@DocumentId
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	, generator = "group_info_table"
	@Column(name = "gid", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "grid")
	public GroupRightInfo getGroupRightInfo() {
		return this.groupRightInfo;
	}

	public void setGroupRightInfo(GroupRightInfo gri) {
		this.groupRightInfo = gri;
	}

	@Column(name = "owner", nullable = false)
	public long getOwner() {
		return this.owner;
	}

	public void setOwner(long owner) {
		this.owner = owner;
	}

	@Column(name = "label", nullable = false)
	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@OneToMany(mappedBy = "groupInfo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Set<GroupUser> getGroupUser() {
		return groupUser;
	}

	public void setGroupUser(Set<GroupUser> groupUser) {
		this.groupUser = groupUser;
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
		GroupInfo other = (GroupInfo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
