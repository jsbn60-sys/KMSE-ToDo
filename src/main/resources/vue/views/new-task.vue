<template id="new-task">
    <app-frame>
        <div class="row d-flex justify-content-center container">
            <div class="col-md-8">
                <div class="card-hover-shadow-2x mb-3 card border-info">
                    <div class="card-header-tab card-header text-center">
                        <div class="card-header-title font-size-lg font-weight-normal"><i class="fa fa-tasks"></i><h5 class="display-5">Create a new Task</h5></div>
                    </div>
                    <div class="scroll-area-sm bg-info">
                        <perfect-scrollbar class="ps-show-limits">
                            <div style="position: static;" class="ps ps--active-y">
                                <div class="ps-content">
                                    <ul class=" list-group list-group-flush">
                                        <li class="list-group-item">
                                            <div class="todo-indicator"></div>
                                            <div class="widget-content p-0">
                                                <div class="widget-content-wrapper">
                                                    <div class="widget-content-left">
                                                        <div class="form-group">
                                                            <label for="title">Title</label>
                                                            <input v-model="title" type="text" class="form-control" id="title" placeholder="Insert the title of your task here">
                                                        </div>
                                                        <div class="form-group">
                                                            <label for="planed">Deadline</label>
                                                            <input v-model="planed" type="date" class="form-control" id="planed" placeholder="Insert a deadline">
                                                        </div>
                                                        <div class="form-group">
                                                            <label for="priority">Priority</label>
                                                            <select v-model="priority" class="form-control" id="priority">
                                                                <option>low</option>
                                                                <option>medium</option>
                                                                <option>high</option>
                                                            </select>
                                                        </div>
                                                        <div class="form-group">
                                                            <label for="selected_category">Category</label>
                                                            <select v-model="selected_category" class="form-control" id="selected_category">
                                                                <option v-for="category in categories" :value="category.id">{{category.name}}</option>
                                                            </select>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </li>
                                    </ul>
                                </div>
                            </div>
                        </perfect-scrollbar>
                    </div>
                    <div class="d-block text-center card-footer"><button class="btn btn-info" v-on:click="newTask()">Create</button></div>
                </div>
            </div>
        </div>


    </app-frame>
</template>
<script>
    Vue.component("new-task", {
        template: "#new-task",
        data: () => ({
            token: localStorage.getItem("token"),
            title: "",
            priority: "",
            planed: "",
            selected_category: "",
            category: "",
            categories: []
        }),
        created() {
                    const token = localStorage.getItem("token");
                    fetch("/api/me/categories?token=" + token, {
                        method: "GET",
                    })
                        .then( async res => {
                            if(res.status === 200) {
                                let resdata = await res.json();
                                console.log(resdata);
                                this.categories = resdata;
                                console.log(this.categories)
                            }
                        })
                        .catch(() => alert("Error while fetching categories"));
                },
        methods: {
            newTask() {
                let token = this.token;
                let title = this.title;
                let priority = this.priority;
                let planed = this.planed;
                let selected_category = this.selected_category;
                console.log(title);
                console.log(priority);
                console.log(planed);
                console.log(selected_category);
                fetch("/api/me/tasks?token=" + token, {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: "title=" + title + "&priority=" + priority + "&planed=" + planed + "&category=" + selected_category
                })
                    .then(async res => { if(res.status === 200) location.pathname = "/tasks" })
                    .catch(() => alert("Error while creating new task"));
            }
        },
    });
</script>
