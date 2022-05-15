
export const goPOST = (address, submitData, setData, setError, method = 'POST') => {

    fetch(address, {
        method: method,
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(submitData)
    })
        .then((response) => {
            if (!response.ok) {
                throw new Error(
                    `This is an HTTP error: The status is ${response.status}`
                );
            }
            return response.json();
        })
        .then((actualData) => {
            if(setData)
                setData(actualData)
            if(setError)
                setError(null)
        })
        .catch((err) => {
            if(setError)
                setError(err.message)
            if(setData)
                setData(null)
        })
        .finally(() => {
            // setLoading(false);
        });
};

export const goDEL = (address, setData, setError) => {

    fetch(address, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then((response) => {
            if (!response.ok) {
                throw new Error(
                    `This is an HTTP error: The status is ${response.status}`
                );
            }
            return response.json();
        })
        .then((actualData) => {
            if(setData)
                setData(actualData)
            if(setError)
                setError(null)
        })
        .catch((err) => {
            if(setError)
                setError(err.message)
            if(setData)
                setData(null)
        })
        .finally(() => {
            // setLoading(false);
        });
};

export const goGET = (address, setData, setError) => {

    fetch(address, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then((response) => {
            if (!response.ok) {
                throw new Error(
                    `This is an HTTP error: The status is ${response.status}`
                );
            }
            return response.json();
        })
        .then((actualData) => {
            if(setData)
                setData(actualData)
            if(setError)
                setError(null)
        })
        .catch((err) => {
            if(setError)
                setError(err.message)
            if(setData)
                setData(null)
        })
        .finally(() => {
            // setLoading(false);
        });
};



