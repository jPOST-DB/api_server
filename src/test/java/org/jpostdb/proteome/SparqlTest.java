package org.jpostdb.proteome;

import org.jpostdb.proteome.model.Const;
import org.jpostdb.proteome.util.HttpUtil;
import org.junit.jupiter.api.Test;

class SparqlTest {

	@Test
	void testDatasets() throws Exception {
		String url = Const.SPARQLIST + "dbi_dataset_table";
		HttpUtil.getJson(url, null);
	}

}
