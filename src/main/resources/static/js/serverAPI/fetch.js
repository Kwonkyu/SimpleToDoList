export async function fetchResult(URL, option = {}, failMessage = "") {
  return await fetch(URL, option)
  .then(response => { return response })
  .catch(error => { alert(`[ Fetch Result Failed ]: ${failMessage} / ${error.message}`); })
}