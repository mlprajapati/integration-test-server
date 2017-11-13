package rnd.web;

import java.io.File;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.vladium.emma.rt.RT;
import com.vladium.util.IProperties;

@Path("/emma")
public class EmmaController {

	public EmmaController() {
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/props")
	public String getAppProperties() {
		IProperties appProperties = RT.getAppProperties();
		return appProperties.toProperties().toString();
	}

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/dump")
	public String dumpCoverage() {
		String coverageOutFile = RT.getAppProperties().getProperty("coverage.out.file");
		RT.dumpCoverageData(new File(coverageOutFile), false);
		return coverageOutFile;
	}
	
}
