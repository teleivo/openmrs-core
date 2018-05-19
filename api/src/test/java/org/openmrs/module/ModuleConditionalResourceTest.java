package org.openmrs.module;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Tests {@link ModuleConditionalResource}.
 */
public class ModuleConditionalResourceTest {
	
	@Test
	public void shouldCreateModuleConditionalResourceForSpecificOpenmrsVersion() {
		
		ModuleConditionalResource resource = new ModuleConditionalResource("/lib/yourmodule-api-1.10.*", "1.10");
		
		assertThat(resource.getPath(), is("/lib/yourmodule-api-1.10.*"));
		assertThat(resource.getOpenmrsPlatformVersion(), is("1.10"));
		assertThat(resource.getModules(), is(empty()));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldNotAllowAddingModulesIfResourceUsesPathAndVersion() {

		ModuleConditionalResource resource = new ModuleConditionalResource("/lib/yourmodule-api-1.10.*", "1.10");

		ModuleConditionalResource.ModuleAndVersion module = new ModuleConditionalResource.ModuleAndVersion();
		module.setModuleId("reporting");
		module.setVersion("1.8");
		resource.getModules().add(module);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAllowBlankPath() {

		new ModuleConditionalResource(" ", "1.10");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAllowBlankOpenmrsVersion() {

		new ModuleConditionalResource("/lib/yourmodule-api-1.10.*", "  ");
	}

	@Test
	public void shouldCreateModuleConditionalResourceForSpecificModules() {

		ModuleConditionalResource.ModuleAndVersion module1 = new ModuleConditionalResource.ModuleAndVersion();
		module1.setModuleId("reporting");
		module1.setVersion("1.8");

		ModuleConditionalResource.ModuleAndVersion module2 = new ModuleConditionalResource.ModuleAndVersion();
		module2.setModuleId("calculation");
		module2.setVersion("1.2");
		
		ModuleConditionalResource resource = new ModuleConditionalResource("/lib/yourmodule-api-1.10.*", module1, module2);

		assertThat(resource.getPath(), is("/lib/yourmodule-api-1.10.*"));
		assertNull(resource.getOpenmrsPlatformVersion());
		assertThat(resource.getModules(), contains(module1, module2));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailToCreateModuleConditionalResourceForSpecificModulesIfNoModulesGiven() {

		new ModuleConditionalResource("/lib/yourmodule-api-1.10.*");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAllowBlankPathForSpecificModules() {

		new ModuleConditionalResource(" ");
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void shouldNotAllowAddingModulesIfResourceUsesModules() {

		ModuleConditionalResource.ModuleAndVersion module1 = new ModuleConditionalResource.ModuleAndVersion();
		module1.setModuleId("reporting");
		module1.setVersion("1.8");

		ModuleConditionalResource resource = new ModuleConditionalResource("/lib/yourmodule-api-1.10.*", module1);

		resource.getModules().add(module1);
	}
}
