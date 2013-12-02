/**
 * 
 */
package org.opengeoportal.harvester.api.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.opengeoportal.harvester.api.domain.PredefinedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

/**
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/test-data-config.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class })
public class PredefinedRepositoryImplTest {
	@Autowired
	private PredefinedRepositoryServiceImpl serviceImpl;

	@Test
	@DatabaseSetup("predefinedRepositoryData.xml")
	public void findAllTest() {
		List<PredefinedRepository> repositories = serviceImpl.findAll();
		List<String> nameList = new ArrayList<String>();
		for (PredefinedRepository repository : repositories) {
			nameList.add(repository.getName());
		}

		assertThat("Returned list must be ordered by name asc", nameList,
				is(equalTo(Arrays.asList("repo A", "repo B", "repo C",
						"repo D", "repo E"))));

	}

	@Test
	@DatabaseSetup("predefinedRepositoryData.xml")
	public void findAllNotInCustomRepositoriesTest() {
		List<PredefinedRepository> repositories = serviceImpl
				.findAllNotInCustomRepositories();
		List<Long> idList = new ArrayList<Long>();
		for (PredefinedRepository repository : repositories) {
			idList.add(repository.getId());
		}

		assertThat(idList, is(equalTo(Arrays.asList(2l, 5l, 4l))));

	}

}
