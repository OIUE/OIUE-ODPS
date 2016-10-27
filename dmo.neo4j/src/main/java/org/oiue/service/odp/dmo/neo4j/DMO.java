package org.oiue.service.odp.dmo.neo4j;

import java.util.List;

import org.oiue.service.odp.dmo.IDMO;
import org.oiue.table.structure.TableModel;

/**
 * 用户实现的各数据库个性化接口方法
 * @author Every
 *
 */
@SuppressWarnings("serial")
public abstract class DMO extends DMO_DB implements IDMO {
	@SuppressWarnings("rawtypes")
	@Override
	public List Query(TableModel tm) throws Throwable {
		return null;
	}

	@Override
	public TableModel QueryObj(TableModel tm) throws Throwable {
		return null;
	}

}