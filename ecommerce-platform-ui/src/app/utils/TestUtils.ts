import { screen, fireEvent } from "@testing-library/react";

/**
 * Method that clicks Submit button.
 */
export function clickSubmitButton() {
    const submitButton = screen.getByTestId("submit-button");
    fireEvent.click(submitButton);
}