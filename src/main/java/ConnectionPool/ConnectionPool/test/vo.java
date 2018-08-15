package ConnectionPool.ConnectionPool.test;

import ConnectionPool.ConnectionPool.anno.Column;

public class vo {
	@Column("cGuid")
	public String id;
	@Column("ccode")
	public String code;
	@Column("cPWD")
	public String pwd;
	@Column("cName")
	public String name;
	@Column("cIdentity")
	public String iden;
	@Override
	public String toString() {
		return "vo [id=" + id + ", code=" + code + ", pwd=" + pwd + ", name=" + name + ", iden=" + iden + "]";
	}
	
}
