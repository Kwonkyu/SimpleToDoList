export const requestOption = {
  post: (data = {}) => {
    return {
      mode: "cors",
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
    };
  },

  delete: () => {
    return {
      mode: "cors",
      method: "DELETE",
    };
  },
  
  put: (data = {}) => {
    return {
      mode: "cors",
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
    };
  },
};
