import React, {useState} from 'react'

export const TemplateForm = () => {

    const [value, setValue] = useState('Test')

    const handleChange = (event) => {
        this.setState(event.target.value);
    }
    const handleSubmit = (event) => {
        alert('A name was submitted: ' + value);
        event.preventDefault();
    }

    return (
        <div>
            <form onSubmit={handleSubmit}>
                <label>
                    Name:
                    <input type="text" value={value} onChange={handleChange}/>
                </label>
                <input type="submit" value="Submit"/>
            </form>
        </div>
    )
}