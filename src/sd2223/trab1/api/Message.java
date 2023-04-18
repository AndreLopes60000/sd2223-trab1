package sd2223.trab1.api;

/**
 * Represents a message in the system.
 */
public class Message {

	private long id;
	private String user;
	private String domain;
	private long creationTime;
	private String text;

	public Message() {
		this.id = -1;
		this.user = null;
		this.domain = null;
		this.creationTime = -1;
		this.text = null;
	}
	public Message(int svNum, String user, String domain, String text) {
		this.id = this.hashCode(svNum, user, domain, text);
		this.user = user;
		this.domain = domain;
		this.creationTime = System.currentTimeMillis();
		this.text = text;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "Message [id=" + id + ", user=" + user + ", domain=" + domain + ", creationTime=" + creationTime
				+ ", text=" + text + "]";
	}

	private long hashCode(int num, String user, String domain, String text) {
		final int prime = 31;
		int result = num;
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		result = prime * result + ((domain == null) ? 0 : domain.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}
}
