package cy.lib.edittext;

import cy.lib.edittext.CyEditText.Validator;

public class PhoneValidator implements Validator{

	@Override
	public boolean validate(String input) {
		return !(input.length() < 11);
	}

	@Override
	public String validateErrorMsg() {
		return "长度不对";
	}

}
