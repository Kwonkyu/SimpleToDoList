export async function fetchResult(URL, option = {}, failMessage = "") {
  return await fetch(URL, option)
  .then(response => { 
    if(!response.ok) {
      throw new Error();
    }
    else return response;
  })
  .catch(error => { alert(`[ Fetch Result Failed ]: ${failMessage} / ${error.message}`); })
}