
package acme.entities.systemconfigurations;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import acme.client.data.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SystemConfiguration extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Pattern(regexp = "[A-Z]{3}")
	@NotBlank
	private String				systemCurrency;

	@Pattern(regexp = "([A-Z]{3},)*[A-Z]{3}")
	@NotBlank
	private String				acceptedCurrencies;
}
