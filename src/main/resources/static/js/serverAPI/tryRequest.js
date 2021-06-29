export async function tryRequest(
  action,
  actionParameter,
  validation,
  validationParameter,
  finisher = (result) => { return result; }
) {
  try {
    const requestResult = await action(actionParameter);
    const validationResult = validation(requestResult, validationParameter);
    if (!validationResult) throw new Error("Validation Failed.");
    else return finisher(requestResult);
  } catch (error) {
    alert("[ Try Request Failed ]: " + error.message);
  }
}
