package com.notenoughrunes.db.queries;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ModeledQuery<T>
{
	
	public abstract String getSql();
	
	public abstract void setParams(PreparedStatement ps) throws SQLException;

	public abstract T convertRow(ResultSet rs) throws SQLException;

}
