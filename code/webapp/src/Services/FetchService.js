
export const goPOST = (address, submitData, setData, setError, method = 'POST', setLoading) => {

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

        })
        .catch((err) => {
            if(setError)
                setError(err.message)

        })
        .finally(() => {
            if(setLoading !== undefined && setLoading !== null)
                setLoading(false);
        });
};

export const goDEL = (address, setData, setError, setLoading) => {

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
            console.log(`I'm in fetch data`)
        })
        .catch((err) => {
            if(setError)
                setError(err.message)
            console.log(`I'm in fetch error in catch ${err.message}`)
        })
        .finally(() => {
            if(setLoading !== undefined && setLoading !== null)
                setLoading(false);
        });
};

export const goGET = (address, setData, setError, setLoading) => {

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

        })
        .catch((err) => {
            if(setError)
                setError(err.message)

        })
        .finally(() => {
            if(setLoading !== undefined && setLoading !== null)
                setLoading(false);
        });
};



